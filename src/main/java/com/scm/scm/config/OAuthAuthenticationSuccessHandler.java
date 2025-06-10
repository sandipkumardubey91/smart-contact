package com.scm.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.scm.scm.entities.Providers;
import com.scm.scm.entities.User;
import com.scm.scm.helpers.AppConstants;
import com.scm.scm.repository.UserRepo;
import com.scm.scm.services.UserService;
import com.scm.scm.services.ImageService;
import com.scm.scm.helpers.Helper;
import com.scm.scm.helpers.Message;
import com.scm.scm.helpers.MessageType;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    // @Autowired
    // private UserService userService;

    @Autowired
    private ImageService imageService;

    public void setImage(DefaultOAuth2User oauthUser, User user) throws Exception {
        String pictureUrl = oauthUser.getAttribute("picture");
        if (pictureUrl != null) {
            MultipartFile profilePic = Helper.downloadImageAsMultipartFile(pictureUrl);

            // Upload the MultipartFile to Cloudinary
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(profilePic, filename);
            user.setCloudinaryImagePublicId(filename);
            user.setProfilePic(fileURL);
        } else {
            throw new Exception("Profile picture URL is missing.");
        }
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        logger.info("OAuthAuthenticationSuccessHandler");

        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info(authorizedClientRegistrationId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info(key + " : " + value);
        });

        // Initialize user object
        User user = new User();
        User existingUser = null;
        user.setUserId(UUID.randomUUID().toString());
        // user.setRole(AppConstants.ROLE_USER);
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("dummy");

        // Handle OAuth providers
        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            user.setEmail(oauthUser.getAttribute("email").toString());
            user.setName(oauthUser.getAttribute("name").toString());
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GOOGLE);
            user.setAbout("This account is created using Google.");
            existingUser = userRepo.findByEmail(user.getEmail()).orElse(null);
            if (existingUser == null || existingUser.getProfilePic() == null) {
                try {
                    setImage(oauthUser, user);
                } catch (Exception e) {
                    System.err.println("Hello");
                }
            } else {
                user.setProfilePic(existingUser.getProfilePic());
            }
        } else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email").toString()
                    : oauthUser.getAttribute("login").toString() + "@gmail.com";
            String picture = oauthUser.getAttribute("avatar_url").toString();
            String name = oauthUser.getAttribute("login").toString();
            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GITHUB);
            user.setAbout("This account is created using GitHub");
        } else {
            logger.info("OAuthAuthenticationSuccessHandler: Unknown provider");
        }

        // Check if the user already exists
        if (existingUser == null) {
            userRepo.save(user);
            logger.info("New user saved: " + user.getEmail());
            existingUser = user;
        } else {
            existingUser.setProfilePic(user.getProfilePic());
            existingUser.setName(user.getName());
            // update other details if needed
            userRepo.save(existingUser); // Save updated user
            logger.info("Existing user updated: " + existingUser.getEmail());
        }

        // Use the existing user to fetch the profile picture from the database
        // request.getSession().setAttribute("userProfilePic",
        // existingUser.getProfilePic());
        // ❌ Reject if user is disabled
        if (!existingUser.isEnabled()) {
            logger.warn("User is disabled: " + existingUser.getEmail());

            // Invalidate session
            request.getSession().invalidate();

            // Clear authentication
            SecurityContextHolder.clearContext();

            HttpSession session = request.getSession();

            session.setAttribute("message",
                    Message.builder()
                            .content("User is disabled , Please contact the admin !")
                            .type(MessageType.red)
                            .build());

            // Redirect to login with error
            new DefaultRedirectStrategy().sendRedirect(request, response,
                    "/login");
            return;
        }

        // ✅ Continue if user is enabled
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

}
