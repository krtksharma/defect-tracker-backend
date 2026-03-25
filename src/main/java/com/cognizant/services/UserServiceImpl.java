package com.cognizant.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cognizant.dto.RegisterRequest;
import com.cognizant.dto.UserDTO;
import com.cognizant.dto.UserPublicDTO;
import com.cognizant.entities.User;
import com.cognizant.repositries.UserRepository;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ── Existing: list all users ──────────────────────────────────────
    @Override
    public List<User> listOfUsers() {
        return (List<User>) userRepository.findAll();
    }

    // ── Existing: login ───────────────────────────────────────────────
    @Override
    public UserDTO authenticateUser(String username, String password) {
        List<User> users = listOfUsers();
        UserDTO userModel = new UserDTO();
        for (User user : users) {
            if (user.getUserName().equals(username)
                    && user.getPassword().equals(password)
                    && !user.isAccountLocked()) {
                userModel.setUserName(user.getUserName());
                userModel.setPassword(user.getPassword());
                userModel.setRole(user.getRole());
                userModel.setAccountLocked(user.isAccountLocked());
                break;
            }
        }
        return userModel;
    }

    // ── NEW: Register ─────────────────────────────────────────────────
    // Returns null on success, or an error message string on failure
    @Override
    public String registerUser(RegisterRequest request) {

        // 1. Validate role — only these 3 are valid
        String role = request.getRole();
        if (role == null || (!role.equals("developer")
                          && !role.equals("tester")
                          && !role.equals("productowner"))) {
            return "Invalid role. Must be developer, tester, or productowner.";
        }

        // 2. Validate username not empty
        String userName = request.getUserName();
        if (userName == null || userName.trim().isEmpty()) {
            return "Username cannot be empty.";
        }

        // 3. Validate password length
        String password = request.getPassword();
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters.";
        }

        // 4. Check username is unique (case-insensitive)
        Optional<User> existing = userRepository.findByUserName(userName.trim());
        if (existing.isPresent()) {
            return "Username '" + userName + "' is already taken. Choose another.";
        }

        // 5. Save new user — account starts unlocked
        User newUser = new User();
        newUser.setUserName(userName.trim());
        newUser.setPassword(password);   // In production: hash this with BCrypt!
        newUser.setRole(role);
        newUser.setAccountLocked(false);

        userRepository.save(newUser);
        return null; // null = success
    }

    // ── NEW: Get users by role ─────────────────────────────────────────
    // Used by tester's "Assign to Developer" dropdown
    @Override
    public List<UserPublicDTO> getUsersByRole(String role) {
        return userRepository.findByRole(role)
                .stream()
                .filter(u -> !u.isAccountLocked())   // only active accounts
                .map(u -> new UserPublicDTO(u.getId(), u.getUserName(), u.getRole()))
                .collect(Collectors.toList());
    }

    // ── NEW: All users public ──────────────────────────────────────────
    @Override
    public List<UserPublicDTO> getAllUsersPublic() {
        return listOfUsers()
                .stream()
                .map(u -> new UserPublicDTO(u.getId(), u.getUserName(), u.getRole()))
                .collect(Collectors.toList());
    }
}
