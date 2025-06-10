package com.scm.scm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scm.scm.entities.Feedback;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    List<Feedback> findByRatingGreaterThanOrderByCreatedAtDesc(int rating);

}
