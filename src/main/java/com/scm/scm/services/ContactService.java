package com.scm.scm.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.scm.scm.entities.Contact;
import com.scm.scm.entities.User;

public interface ContactService {
    Contact save(Contact contact);

    // update contact
    Contact update(Contact contact);

    // get contacts
    // List<Contact> getAll();

    // get contact by id

    Contact getById(String id);

    // delete contact

    void delete(String id);

    List<Contact> getByUserId(String userId);

    Contact getByContactEmail(String email);

    // search contact

    Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy, String order,
            User user);

    Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection);

    Page<Contact> getByUser(User user, Pageable pageable);

    public void deleteAllContactsOfUser(User user);

    void importContacts(MultipartFile file, String email) throws RuntimeException;

    long countByUser(User user);

    long countFavoritesByUser(User user);

    LocalDateTime getLastUpdated(User user);

    List<Contact> getRecentContacts(String userId, int limit);

    public Map<String, Long> getContactsGroupedByDay();

    public Map<String, Long> getContactsGroupedByMonth();

    public Map<String, Long> getContactsGroupedByYear();

    public Map<String, Integer> getContactCountPerUser();

}
