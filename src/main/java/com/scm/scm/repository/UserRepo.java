package com.scm.scm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.scm.entities.User;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByEmailToken(String id);

    List<User> findByNameContainingIgnoreCase(String username);

    List<User> findByPhoneNumberContainingIgnoreCase(String phoneNumber);

    List<User> findByEmailContainingIgnoreCase(String email);

}
