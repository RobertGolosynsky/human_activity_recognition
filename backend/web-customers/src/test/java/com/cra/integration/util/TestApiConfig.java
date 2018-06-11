package com.cra.integration.util;

import com.cra.model.json.request.AuthenticationRequest;

public class TestApiConfig {

    public static final AuthenticationRequest USER_AUTHENTICATION_REQUEST =
            new AuthenticationRequest("user@user.us", "password");
    public static final AuthenticationRequest EXPIRED_AUTHENTICATION_REQUEST =
            new AuthenticationRequest("expired@expired.ex", "expired");
    public static final AuthenticationRequest INVALID_AUTHENTICATION_REQUEST =
            new AuthenticationRequest("user@user.us", "abc123");

//    public static String getAbsolutePath(String relativePath) {
//        return String.format("http://%s:%d/%s/%s", HOSTNAME, PORT, SERVER_CONTEXT, relativePath);
//    }

}
