package com.cognizant.services;

import java.util.List;

import com.cognizant.dto.RegisterRequest;
import com.cognizant.dto.UserDTO;
import com.cognizant.dto.UserPublicDTO;
import com.cognizant.entities.User;

public interface UserService {

    // Existing
    UserDTO      authenticateUser(String username, String password);
    List<User>   listOfUsers();

    // NEW — register a new user, returns error message or null on success
    String       registerUser(RegisterRequest request);

    // NEW — get all developers (for tester's "assign to" dropdown)
    List<UserPublicDTO> getUsersByRole(String role);

    // NEW — get all users as public DTOs (for admin view)
    List<UserPublicDTO> getAllUsersPublic();
}
