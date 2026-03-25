package com.cognizant.dto;

// Safe public user info — never expose passwords to frontend
public class UserPublicDTO {

    private Long   id;
    private String userName;
    private String role;

    public UserPublicDTO() {}

    public UserPublicDTO(Long id, String userName, String role) {
        this.id       = id;
        this.userName = userName;
        this.role     = role;
    }

    public Long   getId()                  { return id; }
    public void   setId(Long id)           { this.id = id; }

    public String getUserName()            { return userName; }
    public void   setUserName(String u)    { this.userName = u; }

    public String getRole()                { return role; }
    public void   setRole(String r)        { this.role = r; }
}
