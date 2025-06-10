package com.scm.scm.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.scm.scm.entities.Contact;
import com.scm.scm.entities.Feedback;
import com.scm.scm.entities.User;
import com.scm.scm.forms.ContactForm;
import com.scm.scm.forms.FeedbackForm;
import com.scm.scm.helpers.Helper;
import com.scm.scm.helpers.Message;
import com.scm.scm.helpers.MessageType;
import com.scm.scm.services.FeedbackService;
import com.scm.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @RequestMapping("/feedback")
    // add contact page: handler
    public String addFeedbackView(Model model) {
        FeedbackForm feedbackForm = new FeedbackForm();

        model.addAttribute("feedbackForm", feedbackForm);
        return "user/feedback";
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public String saveFeedback(@ModelAttribute FeedbackForm feedbackForm, HttpSession session,
            Authentication authentication) {

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        Feedback feedback = new Feedback();
        feedback.setName(user.getName());
        feedback.setEmail(user.getEmail());
        feedback.setSubject(feedbackForm.getSubject());
        feedback.setMessage(feedbackForm.getMessage());
        feedback.setRating(feedbackForm.getRating());

        // uplod karne ka code

        feedbackService.saveFeedback(feedback);
        System.out.println(feedback);

        session.setAttribute("message",
                Message.builder().content("Feedback successfully submitted. You can view feedbacks in the about page!")
                        .type(MessageType.green).build());

        return "redirect:/user/feedback";
    }

    

}
