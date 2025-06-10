package com.scm.scm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.scm.entities.Contact;
import com.scm.scm.entities.Feedback;
import com.scm.scm.repository.FeedbackRepo;
import com.scm.scm.services.FeedbackService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Override
    public Feedback saveFeedback(Feedback feedback) {

        return feedbackRepo.save(feedback);

    }

    @Override
    public List<Feedback> getTopRatedFeedbacks() {
        return feedbackRepo.findByRatingGreaterThanOrderByCreatedAtDesc(3);
    }

    @Override
    public double getAverageRating() {
        List<Feedback> feedbacks = feedbackRepo.findAll();
        return feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public Map<Integer, Long> getRatingDistribution() {
        List<Feedback> feedbacks = feedbackRepo.findAll();

        // Group by rating and count occurrences
        Map<Integer, Long> ratingCount = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting()));

        // Ensure that all ratings from 1 to 5 are present in the map, even with zero
        // count
        for (int i = 1; i <= 5; i++) {
            ratingCount.putIfAbsent(i, 0L); // If no feedback for a rating, set its count to 0
        }

        // Map<Integer, Long> ratingDistribution = getRatingDistribution();

        // for (Map.Entry<Integer, Long> entry : ratingCount.entrySet()) {
        // System.out.println("Rating: " + entry.getKey() + " | Count: " +
        // entry.getValue());
        // }

        return ratingCount;
    }

}
