package com.scm.scm.services;

import java.util.List;
import java.util.Optional;

import com.scm.scm.entities.User;

public interface UserService {
    User saveUser(User user);

    User getUserById(String id);

    User updateUser(User user, String role);

    void deleteUser(String id);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUsers();

    User getUserByEmail(String email);

    List<User> searchByName(String name);

    List<User> searchByPhoneNumber(String name);

    List<User> searchByEmail(String name);

}
