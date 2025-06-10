package com.scm.scm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scm.scm.entities.User;
import com.scm.scm.forms.AdminUserForm;
import com.scm.scm.forms.ContactSearchForm;
import com.scm.scm.helpers.Helper;
import com.scm.scm.repository.ContactRepo;
import com.scm.scm.repository.FeedbackRepo;
import com.scm.scm.repository.MessageRepo;
import com.scm.scm.services.ContactService;
import com.scm.scm.services.FeedbackService;
import com.scm.scm.services.TodoService;
import com.scm.scm.services.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactRepo contactRepository;

    @Autowired
    private ContactService contactService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepo feedbackRepo;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Fetch users and filter out the admin
        List<User> users = userService.getAllUsers();
        List<User> filteredUsers = users.stream()
                .filter(u -> !u.getRole().equalsIgnoreCase("ROLE_ADMIN"))
                .toList();

        // Total contacts and favorite contacts
        long totalContacts = contactRepository.count();
        long favoriteContacts = contactRepository.countByFavorite(true);

        // Contacts grouped by time
        Map<String, Long> contactsPerDay = contactService.getContactsGroupedByDay();
        Map<String, Long> contactsPerMonth = contactService.getContactsGroupedByMonth();
        Map<String, Long> contactsPerYear = contactService.getContactsGroupedByYear();

        // Contact distribution per user
        Map<String, Integer> contactCountPerUser = contactService.getContactCountPerUser();
        int overallContactCount = contactCountPerUser.values().stream().mapToInt(Integer::intValue).sum();

        Map<String, Double> contactPercentagePerUser = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : contactCountPerUser.entrySet()) {
            double percent = (entry.getValue() * 100.0) / overallContactCount;
            contactPercentagePerUser.put(entry.getKey(), percent);
        }

        Map<String, Integer> todoCountPerUser = todoService.getTodoCountPerUser();
        int totalTodos = todoCountPerUser.values().stream().mapToInt(Integer::intValue).sum();

        Map<String, Double> todoPercentagePerUser = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : todoCountPerUser.entrySet()) {
            double percent = (entry.getValue() * 100.0) / totalTodos;
            todoPercentagePerUser.put(entry.getKey(), percent);
        }

        int totalFeedbacks = feedbackRepo.findAll().size();

        double averageRating = feedbackService.getAverageRating();
        Map<Integer, Long> ratingDistribution = feedbackService.getRatingDistribution();

        // Convert to JSON for JavaScript
        ObjectMapper mapper = new ObjectMapper();
        try {
            String ratingDistributionJson = mapper.writeValueAsString(ratingDistribution);
            model.addAttribute("ratingDistributionJson", ratingDistributionJson);
        } catch (Exception e) {
            model.addAttribute("ratingDistributionJson", "{}"); // fallback
        }
        // Add data to model

        model.addAttribute("userCount", filteredUsers.size());
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        model.addAttribute("contactsPerDay", contactsPerDay);
        model.addAttribute("contactsPerMonth", contactsPerMonth);
        model.addAttribute("contactsPerYear", contactsPerYear);
        model.addAttribute("contactPercentagePerUser", contactPercentagePerUser);
        model.addAttribute("todoPercentagePerUser", todoPercentagePerUser);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("totalFeedbacks", totalFeedbacks);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("ratingDistribution", ratingDistribution);

        return "admin/dashboard";
    }

    @RequestMapping("/manage")
    public String manageUser(Model model) {
        List<User> users = userService.getAllUsers();
        List<User> filteredUsers = users.stream()
                .filter(u -> !u.getRole().equalsIgnoreCase("ROLE_ADMIN"))
                .toList();
        model.addAttribute("userCount", filteredUsers.size());
        model.addAttribute("users", filteredUsers);
        model.addAttribute("userSearchForm", new ContactSearchForm());
        return "admin/users";
    }

    @GetMapping("/user/view/{id}")
    public String viewUser(@PathVariable String id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/view_user";
    }

    @GetMapping("/user/edit/{id}")
    public String editUserForm(@PathVariable String id, Model model) {
        User user = userService.getUserById(id);
        // AdminUserForm userForm = new AdminUserForm();
        // userForm.setEnabled(user.isEnabled());
        // userForm.setEmailVerified(user.isEmailVerified());
        // userForm.setPhoneVerified(user.isPhoneVerified());
        // model.addAttribute("userForm", userForm);
        model.addAttribute("user", user);
        return "admin/edit_user";
    }

    @PostMapping("/user/update")
    public String updateUser(@Valid @ModelAttribute User user) {

        // user.setEnabled(userForm.isEnabled());
        // user.setEmailVerified(userForm.isEmailVerified());
        // user.setPhoneVerified(userForm.isEnabled());
        userService.updateUser(user, "ADMIN");
        return "redirect:/admin/manage";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

    @RequestMapping("/manage/search")
    public String searchUser(@ModelAttribute ContactSearchForm userSearchForm, Model model) {

        String field = userSearchForm.getField();
        String value = userSearchForm.getValue();
        List<User> users = null;
        if (field.equalsIgnoreCase("name")) {
            users = userService.searchByName(value);
        } else if (field.equalsIgnoreCase("email")) {
            users = userService.searchByEmail(value);
        } else if (field.equalsIgnoreCase("phone")) {
            users = userService.searchByPhoneNumber(value);
        } else {
            users = userService.getAllUsers();
        }
        List<User> filteredUsers = users.stream()
                .filter(u -> !u.getRole().equalsIgnoreCase("ROLE_ADMIN"))
                .toList();
        model.addAttribute("userCount", filteredUsers.size());
        model.addAttribute("users", filteredUsers);
        model.addAttribute("userSearchForm", userSearchForm);
        return "admin/search";

    }

}