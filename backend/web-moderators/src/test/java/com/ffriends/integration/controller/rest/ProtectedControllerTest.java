package com.ffriends.integration.controller.rest;

import com.ffriends.integration.util.RequestEntityBuilder;
import com.ffriends.integration.util.TestApiConfig;
import com.ffriends.model.json.request.AuthenticationRequest;
import com.ffriends.model.json.response.AuthenticationResponse;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ProtectedControllerTest extends AbstractWebTest {

    private String authenticationToken;

    @Autowired
    private TestRestTemplate client;

    @Value("${ffriends.route.authentication}")
    private String authenticationRoute;

    @Value("${ffriends.route.protected}")
    private String protectedRoute;

    @Test
    public void requestingProtectedWithNoAuthorizationTokenReturnsUnauthorized() throws Exception {
        this.initializeStateForMakingValidProtectedRequest();

        try {
            ResponseEntity<Void> responseEntity = client.exchange(
                    protectedRoute,
                    HttpMethod.GET,
                    buildProtectedRequestEntityWithoutAuthorizationToken(),
                    Void.class
            );
            assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            fail("Should have returned an HTTP 401: Unauthorized status code");
        }
    }

    @Test
    public void requestingProtectedWithUnauthorizedCredentialsReturnsForbidden() throws Exception {
        this.initializeStateForMakingInvalidProtectedRequest();

        try {
            ResponseEntity<Void> responseEntity = client.exchange(
                    protectedRoute,
                    HttpMethod.GET,
                    buildProtectedRequestEntity(),
                    Void.class
            );

            assertThat(responseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
        } catch (Exception e) {
            fail("Should have returned an HTTP 403: Forbidden status code");
        }
    }

    @Test
    public void requestingProtectedWithValidCredentialsReturnsExpected() throws Exception {
        this.initializeStateForMakingValidProtectedRequest();

        ResponseEntity<String> responseEntity = client.exchange(
                protectedRoute,
                HttpMethod.GET,
                buildProtectedRequestEntity(),
                String.class
        );
        String protectedResponse = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

        try {
            assertThat(protectedResponse, is(":O"));
        } catch (Exception e) {
            fail("Should have returned expected response: :O");
        }
    }

    private void initializeStateForMakingValidProtectedRequest() {
        AuthenticationRequest authenticationRequest = TestApiConfig.ADMIN_AUTHENTICATION_REQUEST;

        ResponseEntity<AuthenticationResponse> authenticationResponse = client.postForEntity(
                authenticationRoute,
                authenticationRequest,
                AuthenticationResponse.class
        );

        authenticationToken = authenticationResponse.getBody().getToken();
    }

    private void initializeStateForMakingInvalidProtectedRequest() {
        AuthenticationRequest authenticationRequest = TestApiConfig.MODERATOR_AUTHENTICATION_REQUEST;

        ResponseEntity<AuthenticationResponse> authenticationResponse = client.postForEntity(
                authenticationRoute,
                authenticationRequest,
                AuthenticationResponse.class
        );

        authenticationToken = authenticationResponse.getBody().getToken();
    }

    private HttpEntity<Object> buildProtectedRequestEntity() {
        return RequestEntityBuilder.buildRequestEntityWithoutBody(authenticationToken);
    }

    private HttpEntity<Object> buildProtectedRequestEntityWithoutAuthorizationToken() {
        return RequestEntityBuilder.buildRequestEntityWithoutBodyOrAuthenticationToken();
    }

}
