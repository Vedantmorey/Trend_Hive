package com.project.trendhive.TrendHive.Dto;

public class LoginDto {
    private String email;
    private String password;
    private String accessToken;
    private String tokenType = "Bearer";

    public LoginDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public LoginDto() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}