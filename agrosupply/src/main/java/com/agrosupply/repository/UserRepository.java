package com.agrosupply.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.User;
import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during login and checking duplicate emails
    Optional<User> findByEmail(String email);

    // Admin fetches all users with a specific status (e.g., all PENDING users)
    List<User> findByStatus(UserStatus status);

    // Admin fetches users by role (e.g., all DELIVERY agents)
    List<User> findByRole(Role role);

    // Check if email already registered
    boolean existsByEmail(String email);
}