package com.scm.scm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.scm.entities.Contact;
import com.scm.scm.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    Contact findByEmail(String email);

    // List<Contact> findByUser(User user);

    Page<Contact> findByUser(User user, Pageable pageable);

    Page<Contact> findByUserAndNameContaining(User user, String nameKeyword, Pageable pageable);

    Page<Contact> findByUserAndEmailContaining(User user, String emailKeyword, Pageable pageable);

    Page<Contact> findByUserAndPhoneNumberContaining(User user, String phoneKeyword, Pageable pageable);

    void deleteAllByUser(User user);

    long countByUser(User user);

    @Query("SELECT COUNT(c) FROM Contact c WHERE c.user = :user AND c.favorite = true")
    long countFavoritesByUser(User user);

    @Query("SELECT MAX(c.updatedAt) FROM Contact c WHERE c.user = :user")
    LocalDateTime findLastUpdatedByUser(User user);

    @Query("SELECT c FROM Contact c WHERE c.user.id  = :userId ORDER BY c.createdAt DESC")
    List<Contact> findTop4ByUserOrderByCreatedAtDesc(String userId);

    long count(); // Total number of contacts

    long countByFavorite(boolean favorite); // Count of favorite contacts

    @Query("SELECT FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m-%d') AS day, COUNT(c) "
            + "FROM Contact c GROUP BY FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m-%d') "
            + "ORDER BY day ASC")
    List<Object[]> countContactsGroupedByDay();

    @Query("SELECT FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m') AS month, COUNT(c) "
            + "FROM Contact c GROUP BY FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m') ORDER BY month ASC")
    List<Object[]> countContactsGroupedByMonth();

    @Query("SELECT FUNCTION('YEAR', c.createdAt) AS year, COUNT(c) "
            + "FROM Contact c GROUP BY FUNCTION('YEAR', c.createdAt) ORDER BY year ASC")
    List<Object[]> countContactsGroupedByYear();

    @Query("SELECT c.user.name, COUNT(c) FROM Contact c GROUP BY c.user.name ORDER BY COUNT(c) DESC")
    List<Object[]> countContactsGroupedByUser();

}
