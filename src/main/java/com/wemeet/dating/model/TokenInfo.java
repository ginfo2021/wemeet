package com.wemeet.dating.model;


import com.wemeet.dating.model.enums.TokenType;

import javax.validation.constraints.NotBlank;

public class TokenInfo {
    @NotBlank
    private String accessToken;

    private TokenType tokenType;


    public TokenInfo() {
    }

    public TokenInfo(String accessToken, TokenType tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}

