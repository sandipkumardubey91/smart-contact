package com.scm.scm.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import com.scm.scm.entities.Contact;
import com.scm.scm.entities.User;
import com.scm.scm.services.ContactService;
import com.scm.scm.services.UserService;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Helper {

    // Method to get the email of the logged-in user based on the authentication
    // type
    public static String getEmailOfLoggedInUser(Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken) {

            var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oauth2User = (OAuth2User) authentication.getPrincipal();
            String username = "";

            if (clientId.equalsIgnoreCase("google")) {
                // Fetch email from Google OAuth
                System.out.println("Getting email from Google");
                username = oauth2User.getAttribute("email").toString();

            } else if (clientId.equalsIgnoreCase("github")) {
                // Fetch email from GitHub OAuth
                System.out.println("Getting email from GitHub");
                username = oauth2User.getAttribute("email") != null
                        ? oauth2User.getAttribute("email").toString()
                        : oauth2User.getAttribute("login").toString() + "@gmail.com";
            }

            return username;
        } else {
            // For local authentication (non-OAuth)
            System.out.println("Getting data from local database");
            return authentication.getName();
        }
    }

    // Method to create a verification email link using a token
    public static String getLinkForEmailVerification(String emailToken) {
        String link = "http://localhost:8081/auth/verify-email?token=" + emailToken;
        return link;
    }

    // Method to download an image from a URL and return it as a MultipartFile
    public static MultipartFile downloadImageAsMultipartFile(String imageUrl) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(imageUrl, byte[].class);
        byte[] imageBytes = responseEntity.getBody();

        if (imageBytes != null) {
            return new ByteArrayMultipartFile(imageBytes, "image.jpg"); // Adjust filename/extension as needed
        } else {
            System.err.println("Failed to download image from URL: " + imageUrl);
            throw new Exception("Failed to download image.");
        }
    }

    // Helper class to convert byte array to MultipartFile
    public static class ByteArrayMultipartFile implements MultipartFile {
        private byte[] content;
        private String filename;

        public ByteArrayMultipartFile(byte[] content, String filename) {
            this.content = content;
            this.filename = filename;
        }

        @Override
        public String getName() {
            return filename;
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            String contentType = "image/jpeg"; // Default
            try {
                contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(content));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
