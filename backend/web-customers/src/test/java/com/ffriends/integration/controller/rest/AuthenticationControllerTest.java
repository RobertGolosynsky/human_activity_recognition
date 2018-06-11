package com.ffriends.integration.controller.rest;

import com.ffriends.integration.util.RequestEntityBuilder;
import com.ffriends.integration.util.TestApiConfig;
import com.ffriends.model.json.request.AuthenticationRequest;
import com.ffriends.model.json.response.AuthenticationResponse;
import com.ffriends.service.TokenUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class AuthenticationControllerTest  extends AbstractWebTest{

  @Autowired
  private TestRestTemplate client;

  private AuthenticationRequest authenticationRequest;
  private String authenticationToken;

  @Value("${ffriends.route.authentication}")
  private String authenticationRoute;

  @Value("${ffriends.route.authentication.refresh}")
  private String refreshRoute;

  @Autowired
  private TokenUtils tokenUtils;

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void requestingAuthenticationWithNoCredentialsReturnsBadRequest() throws Exception {
    this.initializeStateForMakingValidAuthenticationRequest();

    try {
      ResponseEntity<Void> responseEntity = client.exchange(
        authenticationRoute,
        HttpMethod.POST,
        buildAuthenticationRequestEntityWithoutCredentials(),
        Void.class
      );

      assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    } catch (Exception e) {
      fail(e.toString());
      fail("Should have returned an HTTP 400: Bad Request status code");
    }
  }

  @Test
  public void requestingAuthenticationWithInvalidCredentialsReturnsUnauthorized() throws Exception {
    this.initializeStateForMakingInvalidAuthenticationRequest();

    try {
      ResponseEntity<Void> responseEntity = client.exchange(
        authenticationRoute,
        HttpMethod.POST,
        buildAuthenticationRequestEntity(),
        Void.class
      );
      assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    } catch (Exception e) {
      fail("Should have returned an HTTP 401: Unauthorized status code");
    }
  }

  @Test
  public void requestingProtectedWithValidCredentialsReturnsExpected() throws Exception {
    this.initializeStateForMakingValidAuthenticationRequest();

    ResponseEntity<AuthenticationResponse> responseEntity = client.exchange(
      authenticationRoute,
      HttpMethod.POST,
      buildAuthenticationRequestEntity(),
      AuthenticationResponse.class
    );
    AuthenticationResponse authenticationResponse = responseEntity.getBody();

    try {
      assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    } catch (Exception e) {
      fail("Should have returned an HTTP 400: Ok status code");
    }

    try {
      assertThat(this.tokenUtils.getUsernameFromToken(authenticationResponse.getToken()), is(authenticationRequest.getUsername()));
    } catch (Exception e) {
      fail("Should have returned expected username from token");
    }
  }

  @Test
  public void requestingAuthenticationRefreshWithNoAuthorizationTokenReturnsUnauthorized() throws Exception {
    this.initializeStateForMakingValidAuthenticationRefreshRequest();

    try {
      ResponseEntity<Void> responseEntity = client.exchange(
        String.format("%s/%s", authenticationRoute, refreshRoute),
        HttpMethod.GET,
        buildAuthenticationRefreshRequestEntityWithoutAuthorizationToken(),
        Void.class
      );
      assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    } catch (Exception e) {
      fail("Should have returned an HTTP 401: Unauthorized status code");
    }
  }


  @Test
  public void requestingAuthenticationRefreshTokenWithTokenCreatedBeforeLastPasswordResetReturnsBadRequest() throws Exception {
    this.initializeStateForMakingExpiredAuthenticationRefreshRequest();

    try {
      ResponseEntity<Void> responseEntity = client.exchange(
        String.format("%s/%s", authenticationRoute, refreshRoute),
        HttpMethod.GET,
        buildAuthenticationRefreshRequestEntity(),
        Void.class
      );

      assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    } catch (Exception e) {
      fail("Should have returned an HTTP 400: Bad Request status code");
    }
  }

  private void initializeStateForMakingValidAuthenticationRequest() {
    authenticationRequest = TestApiConfig.USER_AUTHENTICATION_REQUEST;
  }

  private void initializeStateForMakingInvalidAuthenticationRequest() {
    authenticationRequest = TestApiConfig.INVALID_AUTHENTICATION_REQUEST;
  }

  private void initializeStateForMakingValidAuthenticationRefreshRequest() {
    authenticationRequest = TestApiConfig.USER_AUTHENTICATION_REQUEST;

    ResponseEntity<AuthenticationResponse> authenticationResponse = client.postForEntity(
      authenticationRoute,
      authenticationRequest,
      AuthenticationResponse.class
    );

    authenticationToken = authenticationResponse.getBody().getToken();
  }

  private void initializeStateForMakingInvalidAuthenticationRefreshRequest() {
    authenticationRequest = TestApiConfig.INVALID_AUTHENTICATION_REQUEST;

    ResponseEntity<AuthenticationResponse> authenticationResponse = client.postForEntity(
      authenticationRoute,
      authenticationRequest,
      AuthenticationResponse.class
    );

    authenticationToken = authenticationResponse.getBody().getToken();
  }

  private void initializeStateForMakingExpiredAuthenticationRefreshRequest() {
    authenticationRequest = TestApiConfig.EXPIRED_AUTHENTICATION_REQUEST;

    ResponseEntity<AuthenticationResponse> authenticationResponse = client.postForEntity(
      authenticationRoute,
      authenticationRequest,
      AuthenticationResponse.class
    );

    authenticationToken = authenticationResponse.getBody().getToken();
  }

  private HttpEntity<Object> buildAuthenticationRequestEntity() {
    return RequestEntityBuilder.buildRequestEntityWithoutAuthenticationToken(authenticationRequest);
  }

  private HttpEntity<Object> buildAuthenticationRequestEntityWithoutCredentials() {
    return RequestEntityBuilder.buildRequestEntityWithoutBodyOrAuthenticationToken();
  }

  private HttpEntity<Object> buildAuthenticationRefreshRequestEntity() {
    return RequestEntityBuilder.buildRequestEntityWithoutBody(authenticationToken);
  }

  private HttpEntity<Object> buildAuthenticationRefreshRequestEntityWithoutAuthorizationToken() {
    return RequestEntityBuilder.buildRequestEntityWithoutBodyOrAuthenticationToken();
  }

}
