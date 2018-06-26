package com.cra.model.json.response;

import lombok.Getter;

@Getter
public class CRAErrorResponse {

    private String error;

    public CRAErrorResponse(String error) {
        this.error = error;
    }
}
