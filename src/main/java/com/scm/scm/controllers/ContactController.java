package com.scm.scm.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.scm.scm.entities.Contact;
import com.scm.scm.entities.User;
import com.scm.scm.forms.ContactForm;
import com.scm.scm.forms.ContactSearchForm;
import com.scm.scm.helpers.AppConstants;
import com.scm.scm.helpers.ExcelHelper;
import com.scm.scm.helpers.Helper;
import com.scm.scm.helpers.Message;
import com.scm.scm.helpers.MessageType;
import com.scm.scm.services.ContactService;
import com.scm.scm.services.ImageService;
import com.scm.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @RequestMapping("/add")
    // add contact page: handler
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();

        contactForm.setFavorite(true);
        // model.addAttribute("navbarPadding", "my-custom-pl-64");
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @RequestMapping("/export")
    public ResponseEntity<byte[]> exportContact(Authentication authentication, HttpSession session) throws Exception {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);
        List<Contact> contacts = contactService.getByUserId(user.getUserId());
        if (contacts == null || contacts.isEmpty()) {
            // Respond with 204 No Content and no body
            session.setAttribute("message", Message.builder()
                    .content("No contacts to export!")
                    .type(MessageType.red)
                    .build());
            return ResponseEntity
                    .status(302)
                    .header("Location", "/user/contacts")
                    .build();
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // Create a workbook and write contacts to it
            // Workbook workbook = new XSSFWorkbook();
            ExcelHelper.exportToExcel(contacts, byteArrayOutputStream);

            // Prepare the file as byte array
            byte[] fileContent = byteArrayOutputStream.toByteArray();

            // Set up headers for download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=contacts.xlsx");

            // Return the response with the file content and headers for download
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileContent.length)
                    .body(fileContent);
        } catch (IOException e) {
            System.err.println("Error during file export: " + e.getMessage());
            // Handle error and possibly set a model attribute for displaying an error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
    public String importContacts(@RequestParam("file") MultipartFile file, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            contactService.importContacts(file, Helper.getEmailOfLoggedInUser(authentication));
            redirectAttributes.addFlashAttribute("message", "Contacts imported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error importing contacts: " + e.getMessage());
        }
        return "redirect:/user/contacts";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {

        // process the form data

        // 1 validate form

        if (result.hasErrors()) {

            // result.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        // form ---> contact

        User user = userService.getUserByEmail(username);
        // 2 process the contact picture

        // image process

        // uplod karne ka code
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());

        if (contactForm.getContactImage() != null &&
                !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(),
                    filename);
            contact.setCloudinaryImagePublicId(filename);
            contact.setPicture(fileURL);
        }

        contactService.save(contact);
        System.out.println(contactForm);

        // 3 set the contact picture url

        // 4 `set message to be displayed on the view

        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts";

    }

    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {

        // load all the user contacts
        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        // model.addAttribute("navbarPadding", "my-custom-pl-64");

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // search handler

    @RequestMapping("/search")
    public String searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {

        String field = contactSearchForm.getField();
        String value = contactSearchForm.getValue();

        logger.info("field {} keyword {}", field, value);

        User user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContact = null;
        if (field.equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(value, size, page, sortBy, direction, user);
        } else if (field.equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(value, size, page, sortBy, direction, user);
        } else if (field.equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(value, size, page, sortBy, direction, user);
        } else {
            pageContact = contactService.getByUser(user, page, size, sortBy, direction);
        }

        logger.info("PageContact {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        // model.addAttribute("navbarPadding", "my-custom-pl-64");

        model.addAttribute("pageContact", pageContact);

        return "user/search";
    }

    // detete contact
    @RequestMapping("/delete/{contactId}")
    public String deleteContact(
            @PathVariable("contactId") String contactId,
            HttpSession session) {
        contactService.delete(contactId);
        logger.info("contactId {} deleted", contactId);

        session.setAttribute("message",
                Message.builder()
                        .content("Contact is Deleted successfully !! ")
                        .type(MessageType.green)
                        .build()

        );

        return "redirect:/user/contacts";
    }

    @RequestMapping("/delete/all")
    public String deleteAllContact(
            HttpSession session, Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        if (user.getContacts().isEmpty()) {
            session.setAttribute("message",
                    Message.builder()
                            .content("You have no Contacts available !! ")
                            .type(MessageType.red)
                            .build()

            );
        }

        else {
            contactService.deleteAllContactsOfUser(user);

            session.setAttribute("message",
                    Message.builder()
                            .content("All Contacts deleted successfully !! ")
                            .type(MessageType.green)
                            .build()

            );
        }

        return "redirect:/user/contacts";
    }

    // update contact form view

    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
            @PathVariable("contactId") String contactId, Model model) {

        var contact = contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setAddress(contact.getAddress());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setDescription(contact.getDescription());
        contactForm.setPicture(contact.getPicture());
        contactForm.setFavorite(contact.isFavorite());

        model.addAttribute("contactId", contactId);
        model.addAttribute("contactForm", contactForm);
        // model.addAttribute("navbarPadding", "my-custom-pl-64");

        return "user/update_contact_view";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(
            @PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm,
            BindingResult result,
            Model model,
            HttpSession session) {

        // update the contact
        var con = contactService.getById(contactId);

        if (result.hasErrors()) {
            return "user/update_contact_view";
        }
        // con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setLinkedInLink(contactForm.getLinkedInLink());
        con.setWebsiteLink(contactForm.getWebsiteLink());
        con.setFavorite(contactForm.isFavorite());

        // process image
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImagePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);
        }

        var updateCon = contactService.update(con);
        logger.info("updatedCon {}", updateCon);
        session.setAttribute("message",
                Message.builder().content("Contact Updated Successfully !!").type(MessageType.green).build());

        return "redirect:/user/contacts";

    }

    @RequestMapping("/import")
    public String contactImport(Model model) {
        // model.addAttribute("navbarPadding", "my-custom-pl-64");
        return "user/import";
    }

    @RequestMapping("/unknownUser/add/{userId}")
    public String addUnknownUser(@PathVariable("userId") String userId, Model model) {
        ContactForm contactForm = new ContactForm();
        User user = userService.getUserById(userId);
        contactForm.setFavorite(true);
        contactForm.setPicture(user.getProfilePic());
        contactForm.setPhoneNumber(user.getPhoneNumber());
        contactForm.setName(user.getName());
        contactForm.setEmail(user.getEmail());
        // model.addAttribute("navbarPadding", "my-custom-pl-64");
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

}
