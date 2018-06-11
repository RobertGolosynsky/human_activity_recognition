package com.ffriends.integration.util;

import com.ffriends.model.json.request.AuthenticationRequest;

public class TestApiConfig {

    public static final AuthenticationRequest ADMIN_AUTHENTICATION_REQUEST =
            new AuthenticationRequest("admin@admin.ad", "admin");
    public static final AuthenticationRequest EXPIRED_AUTHENTICATION_REQUEST =
            new AuthenticationRequest("expired@exp.ex", "expired");
    public static final AuthenticationRequest INVALID_AUTHENTICATION_REQUEST =
            new AuthenticationRequest("admin@admin.ad", "abc123");
    public static final AuthenticationRequest MODERATOR_AUTHENTICATION_REQUEST =
            new AuthenticationRequest("moderator@moder.mo", "moderator");

//    public static String getAbsolutePath(String relativePath) {
//        return String.format("http://%s:%d/%s/%s", HOSTNAME, PORT, SERVER_CONTEXT, relativePath);
//    }

}
