package org.cra.contextrecognition.network.domain;

public class CRAAuthResponse {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "CRAAuthResponse{" +
                "token='" + token + '\'' +
                '}';
    }
}
