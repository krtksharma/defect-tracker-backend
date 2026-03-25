package com.cognizant.repositries;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    // Existing — used by login
    User findByUserNameAndPassword(String userName, String password);

    // NEW — check if username already exists (for registration duplicate check)
    Optional<User> findByUserName(String userName);

    // NEW — get all users of a specific role (e.g., "developer")
    List<User> findByRole(String role);
}
