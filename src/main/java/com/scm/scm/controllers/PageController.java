package com.scm.scm.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.scm.scm.entities.Feedback;
import com.scm.scm.entities.User;
import com.scm.scm.forms.UserForm;
import com.scm.scm.helpers.Helper;
import com.scm.scm.helpers.Message;
import com.scm.scm.helpers.MessageType;
import com.scm.scm.services.FeedbackService;
import com.scm.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        // model.addAttribute("name", "Sandip Kumar Dubey");
        // model.addAttribute("Youtube_Channel", "The OfflineShow");
        // model.addAttribute("github", "https://github.com/sandipkumardubey91");
        // model.addAttribute("navbarPadding", "my-custom-pl-16");
        return "Home";
    }

    // about route
    // @RequestMapping("/about")
    // public String aboutPage(Model model) {
    // // model.addAttribute("isLogin", false);
    // return "about";
    // }

    @RequestMapping("/about")
    public String showTopRatedFeedbacks(Model model) {
        List<Feedback> topRatedFeedbacks = feedbackService.getTopRatedFeedbacks();
        model.addAttribute("topRatedFeedbacks", topRatedFeedbacks);
        // model.addAttribute("navbarPadding", "my-custom-pl-16");
        return "about"; // Points to templates/user/showFeedbacks.html
    }

    // services route
    @RequestMapping("/services")
    public String servicesPage(Model model) {

        // model.addAttribute("navbarPadding", "my-custom-pl-16");
        return "services";
    }

    // contact page
    @GetMapping("/contact")
    public String contactPage(Model model) {

        // model.addAttribute("navbarPadding", "my-custom-pl-16");
        return "contact";
    }

    // login page
    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    // register page
    @GetMapping("/register")
    public String register(Model model) {

        UserForm userForm = new UserForm();
        // default data bhi daal sakte hai
        // userForm.setName("Sandip");
        // userForm.setAbout("This is about : Write something about yourself");
        model.addAttribute("userForm", userForm);

        return "register";
    }

    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {
        System.out.println(
                "************************************************************************************************************Processing registration");
        System.out.println(userForm);
        // validate form data
        if (rBindingResult.hasErrors()) {
            return "register";
        }

        // User user = User.builder()
        // .name(userForm.getName())
        // .email(userForm.getEmail())
        // .password(userForm.getPassword())
        // .about(userForm.getAbout())
        // .phoneNumber(userForm.getPhoneNumber())
        // .profilePic(
        // "https://www.learncodewithdurgesh.com/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fdurgesh_sir.35c6cb78.webp&w=1920&q=75")
        // .build();

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setEnabled(true);
        user.setProfilePic(null);

        // User savedUser = userService.saveUser(user);
        System.out.println("user saved :");
        userService.saveUser(user);
        Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();

        session.setAttribute("message", message);
        return "redirect:/login";
    }

    // @GetMapping("/default")
    // public String defaultAfterLogin(Authentication auth) {
    // if (auth.getAuthorities().stream()
    // .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
    // return "redirect:/admin/dashboard";
    // }
    // return "redirect:/user/dashboard";
    // }

}
