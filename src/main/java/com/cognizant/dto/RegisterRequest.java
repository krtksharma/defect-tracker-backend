package com.cognizant.dto;

// DTO for new user registration
// Sent from frontend Register page
public class RegisterRequest {

    private String userName;
    private String password;
    private String role;  // "developer", "tester", "productowner"

    public RegisterRequest() {}

    public RegisterRequest(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public String getUserName()              { return userName; }
    public void   setUserName(String u)      { this.userName = u; }

    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }

    public String getRole()                  { return role; }
    public void   setRole(String r)          { this.role = r; }
}
