package com.scm.scm.services.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import com.scm.scm.entities.Contact;
import com.scm.scm.helpers.ExcelHelper;
import com.scm.scm.helpers.ResourceNotFoundException;
import com.scm.scm.repository.ContactRepo;
import com.scm.scm.services.ContactService;
import com.scm.scm.services.UserService;
import com.scm.scm.entities.User;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserService userService;

    @Override
    public Contact save(Contact contact) {

        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);
        return contactRepo.save(contact);

    }

    @Override
    public Contact update(Contact contact) {

        var contactOld = contactRepo.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setDescription(contact.getDescription());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setAddress(contact.getAddress());
        contactOld.setPicture(contact.getPicture());
        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactRepo.save(contactOld);

    }

    // @Override
    // public List<Contact> getAll() {
    // return contactRepo.findAll();
    // }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    @Override
    public Page<Contact> getByUser(User user, Pageable pageable) {
        return contactRepo.findByUser(user, pageable);
    }

    @Transactional
    @Override
    public void delete(String id) {
        Contact contact = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));

        // Remove contact from user's list and break reference
        User user = contact.getUser();
        if (user != null) {
            user.getContacts().removeIf(c -> c.getId().equals(contact.getId()));
            contact.setUser(null);
        }

        contactRepo.delete(contact);
        // contactRepo.flush(); // Ensure immediate DB operation
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        return contactRepo.findByUserId(userId);
    }

    @Override
    public Contact getByContactEmail(String email) {
        return contactRepo.findByEmail(email);
    }

    @Transactional
    @Override
    public void deleteAllContactsOfUser(User user) {
        if (user != null) {
            user.getContacts().clear();
        }
        contactRepo.deleteAllByUser(user);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        var pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByUser(user, pageable);

    }

    @Override
    public Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndNameContaining(user, nameKeyword, pageable);

    }

    @Override
    public Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order,
            User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndEmailContaining(user, emailKeyword, pageable);

    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy,
            String order, User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndPhoneNumberContaining(user, phoneNumberKeyword, pageable);

    }

    @Override
    public void importContacts(MultipartFile file, String email) throws RuntimeException {

        DataFormatter formatter = new DataFormatter();

        try (InputStream is = file.getInputStream();
                Workbook workbook = file.getOriginalFilename().endsWith(".xlsx") ? new XSSFWorkbook(is)
                        : new HSSFWorkbook(is);) {

            Sheet sheet = workbook.getSheetAt(0);

            User user = userService.getUserByEmail(email);

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // skip header

                Contact contact = new Contact();
                String phoneNumber = formatter.formatCellValue(row.getCell(2));

                contact.setName(ExcelHelper.getCellValue(row, 0));
                contact.setEmail(ExcelHelper.getCellValue(row, 1));
                contact.setPhoneNumber(phoneNumber);
                contact.setAddress(ExcelHelper.getCellValue(row, 3));
                contact.setPicture(ExcelHelper.getCellValue(row, 4));
                contact.setDescription(ExcelHelper.getCellValue(row, 5));
                contact.setWebsiteLink(ExcelHelper.getCellValue(row, 6));
                contact.setLinkedInLink(ExcelHelper.getCellValue(row, 7));
                // contact.setCloudinaryImagePublicId(ExcelHelper.getCellValue(row, 9));
                if (ExcelHelper.getCellValue(row, 8).equals("TRUE")) {
                    contact.setFavorite(true);
                } else {
                    contact.setFavorite(false);
                }
                contact.setUser(user);

                this.save(contact);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to import contacts", e);
        }
    }

    @Override
    public long countByUser(User user) {
        return contactRepo.countByUser(user);
    }

    @Override
    public long countFavoritesByUser(User user) {
        return contactRepo.countFavoritesByUser(user);
    }

    @Override
    public LocalDateTime getLastUpdated(User user) {
        return contactRepo.findLastUpdatedByUser(user);
    }

    @Override
    public List<Contact> getRecentContacts(String userId, int limit) {
        List<Contact> contacts = contactRepo.findTop4ByUserOrderByCreatedAtDesc(userId);
        return contacts.size() > limit ? contacts.subList(0, limit) : contacts;
    }

    @Override
    public Map<String, Long> getContactsGroupedByDay() {
        List<Object[]> results = contactRepo.countContactsGroupedByDay();
        Map<String, Long> groupedData = new LinkedHashMap<>();

        for (Object[] row : results) {
            String day = (String) row[0];
            Long count = (Long) row[1];
            groupedData.put(day, count);
        }

        return groupedData;
    }

    @Override
    public Map<String, Long> getContactsGroupedByMonth() {
        List<Object[]> results = contactRepo.countContactsGroupedByMonth();
        Map<String, Long> groupedData = new LinkedHashMap<>();

        for (Object[] row : results) {
            String month = (String) row[0];
            Long count = (Long) row[1];
            groupedData.put(month, count);
        }

        return groupedData;
    }

    @Override
    public Map<String, Long> getContactsGroupedByYear() {
        List<Object[]> results = contactRepo.countContactsGroupedByYear();
        Map<String, Long> groupedData = new LinkedHashMap<>();

        for (Object[] row : results) {
            String year = String.valueOf(row[0]);
            Long count = (Long) row[1];
            groupedData.put(year, count);
        }

        return groupedData;
    }

    public Map<String, Integer> getContactCountPerUser() {
        List<Object[]> result = contactRepo.countContactsGroupedByUser();
        Map<String, Integer> userContacts = new LinkedHashMap<>();
        for (Object[] row : result) {
            String username = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            userContacts.put(username, count);
        }
        return userContacts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

}
