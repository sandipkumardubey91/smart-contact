package com.scm.scm.services;

import java.util.List;
import java.util.Map;

import com.scm.scm.entities.Feedback;

public interface FeedbackService {
    Feedback saveFeedback(Feedback feedback);

    List<Feedback> getTopRatedFeedbacks(); // rating > 4

    public double getAverageRating();

    public Map<Integer, Long> getRatingDistribution();

}
