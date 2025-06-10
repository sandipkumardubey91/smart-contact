package com.scm.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.scm.entities.User;
import com.scm.scm.helpers.AppConstants;
import com.scm.scm.helpers.Helper;
import com.scm.scm.helpers.ResourceNotFoundException;
import com.scm.scm.repository.UserRepo;
import com.scm.scm.services.EmailService;
import com.scm.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public User saveUser(User user) {
        // user id : have to generate
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        // password encode
        // user.setPassword(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // set the user role
        // user.setRole(AppConstants.ROLE_USER);

        logger.info(user.getProvider().toString());

        // String emailToken = UUID.randomUUID().toString();
        // String emailLink = Helper.getLinkForEmailVerification(emailToken);

        // user.setEmailToken(emailToken);

        // emailService.sendEmail(user.getEmail(), "Verify Account : Smart Contact
        // Manager", emailLink);

        return userRepo.save(user);

    }

    @Override
    public User getUserById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    @Override
    public User updateUser(User user, String role) {
        User user2 = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (role.equals("USER")) {
            user2.setName(user.getName());
            user2.setAbout(user.getAbout());
            user2.setPhoneNumber(user.getPhoneNumber());
            user2.setFacebookLink(user.getFacebookLink());
            user2.setLinkedinLink(user.getLinkedinLink());
            user2.setGithubLink(user.getGithubLink());
            user2.setTwitterLink(user.getTwitterLink());
            user2.setProfilePic(user.getProfilePic());
            user2.setCloudinaryImagePublicId(user.getCloudinaryImagePublicId());

        } else {
            user2.setEnabled(user.isEnabled());
            user2.setEmailVerified(user.isEmailVerified());
            user2.setPhoneVerified(user.isPhoneVerified());
        }
        // user2.setProvider(user.getProvider());
        // user2.setProviderUserId(user.getProviderUserId());

        return userRepo.save(user2);
    }

    @Override
    public void deleteUser(String id) {
        User user2 = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepo.delete(user2);

    }

    @Override
    public boolean isUserExist(String userId) {
        User user2 = userRepo.findById(userId).orElse(null);
        return user2 != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);

    }

    @Override
    public List<User> searchByName(String name) {

        return userRepo.findByNameContainingIgnoreCase(name);

    }

    @Override
    public List<User> searchByPhoneNumber(String number) {

        return userRepo.findByPhoneNumberContainingIgnoreCase(number);

    }

    @Override
    public List<User> searchByEmail(String email) {

        return userRepo.findByEmailContainingIgnoreCase(email);

    }

}
