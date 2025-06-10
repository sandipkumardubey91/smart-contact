package com.scm.scm.controllers;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scm.scm.entities.Contact;
import com.scm.scm.entities.Todo;
import com.scm.scm.entities.User;
import com.scm.scm.forms.ContactForm;
import com.scm.scm.forms.UserForm;
import com.scm.scm.helpers.Helper;
import com.scm.scm.helpers.Message;
import com.scm.scm.helpers.MessageType;
import com.scm.scm.services.ContactService;
import com.scm.scm.services.ImageService;
import com.scm.scm.services.TodoService;
import com.scm.scm.services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    // private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private TodoService todoService;

    // user dashbaord page

    // private Logger

    @GetMapping("/dashboard")
    public String getDashboard(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(email);

        long totalContacts = contactService.countByUser(loggedInUser);
        long favouriteContacts = contactService.countFavoritesByUser(loggedInUser);
        int upcomingReminders = todoService.getTodosByUserId(loggedInUser.getUserId()).size();
        
        List<Contact> recentContacts = contactService.getRecentContacts(loggedInUser.getUserId(), 4);
        LocalDateTime lastUpdated = contactService.getLastUpdated(loggedInUser);

        List<Todo> todo = todoService.getTodosByUserId(loggedInUser.getUserId());

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favouriteContacts", favouriteContacts);
        model.addAttribute("upcomingReminders", upcomingReminders);
        model.addAttribute("lastUpdated", lastUpdated);
        model.addAttribute("recentContacts", recentContacts);

        model.addAttribute("todos", todo);

        return "user/dashboard";
    }

    // user profile page

    @RequestMapping(value = "/profile")
    public String userProfile(Authentication authentication, Model model) {

        // String name = principal.getName();
        // String username = Helper.getEmailOfLoggedInUser(authentication);
        // logger.info("User logged in: {}", username);
        // User user = userService.getUserByEmail(username);

        // // System.out.println(user.getName());
        // // System.out.println(user.getEmail());
        // model.addAttribute("loggedInUser", user);
        // model.addAttribute("navbarPadding", "my-custom-pl-64");
        return "user/profile";
    }

    // edit profile page
    @GetMapping("/edit")
    public String updateUserFormView(
            Authentication authentication, Model model) {

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        UserForm userForm = new UserForm();
        userForm.setName(user.getName());
        userForm.setAbout(user.getAbout());
        userForm.setPhoneNumber(user.getPhoneNumber());
        userForm.setFacebookLink(user.getFacebookLink());
        userForm.setLinkedinLink(user.getLinkedinLink());
        userForm.setTwitterLink(user.getTwitterLink());
        userForm.setGithubLink(user.getGithubLink());
        userForm.setPicture(user.getProfilePic());

        // userForm.setEmail(user.getEmail());
        // userForm.setPassword(user.getPassword());

        // model.addAttribute("navbarPadding", "my-custom-pl-64");
        model.addAttribute("userForm", userForm);

        return "user/profile_edit";
    }

    // update profile handler
    @PostMapping("/edit")
    public String handleProfileUpdate(
            @ModelAttribute("userForm") UserForm userForm,
            Authentication authentication,
            HttpSession session) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User existingUser = userService.getUserByEmail(email);

        // Update fields from form
        existingUser.setName(userForm.getName());
        existingUser.setAbout(userForm.getAbout());
        existingUser.setPhoneNumber(userForm.getPhoneNumber());
        existingUser.setFacebookLink(userForm.getFacebookLink());
        existingUser.setLinkedinLink(userForm.getLinkedinLink());
        existingUser.setTwitterLink(userForm.getTwitterLink());
        existingUser.setGithubLink(userForm.getGithubLink());

        if (userForm.getUserImage() != null && !userForm.getUserImage().isEmpty()) {
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(userForm.getUserImage(), fileName);
            existingUser.setCloudinaryImagePublicId(fileName);
            existingUser.setProfilePic(imageUrl);
            userForm.setPicture(imageUrl);
        }

        // Optional: handle password update logic
        // if (userForm.getPassword() != null && !userForm.getPassword().isBlank()) {
        // String encodedPassword = passwordEncoder.encode(userForm.getPassword());
        // existingUser.setPassword(encodedPassword);
        // }

        userService.updateUser(existingUser, "USER");

        return "redirect:/user/profile";
    }

    @GetMapping("/feedback")
    public String feedbackPage(Model model) {
        // model.addAttribute("navbarPadding", "my-custom-pl-64");
        return "user/feedback";
    }

}
