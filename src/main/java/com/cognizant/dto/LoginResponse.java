package com.cognizant.dto;

/** Returned by POST /api/users/login — contains JWT token + user info */
public class LoginResponse {

    private String token;      // JWT — frontend stores this
    private String userName;
    private String role;
    private String tokenType = "Bearer";

    public LoginResponse() {}

    public LoginResponse(String token, String userName, String role) {
        this.token    = token;
        this.userName = userName;
        this.role     = role;
    }

    public String getToken()                 { return token; }
    public void   setToken(String t)         { this.token = t; }

    public String getUserName()              { return userName; }
    public void   setUserName(String u)      { this.userName = u; }

    public String getRole()                  { return role; }
    public void   setRole(String r)          { this.role = r; }

    public String getTokenType()             { return tokenType; }
    public void   setTokenType(String t)     { this.tokenType = t; }
}
