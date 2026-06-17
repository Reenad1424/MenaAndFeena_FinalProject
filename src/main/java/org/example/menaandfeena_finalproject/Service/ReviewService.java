package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Review;
import org.example.menaandfeena_finalproject.Repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public void addReview(Review review) {
        reviewRepository.save(review);
    }

    public void updateReview(Integer id, Review review) {

        Review oldReview = reviewRepository.findReviewById(id);

        if (oldReview == null) {
            throw new ApiException("Review not found");
        }

        oldReview.setComment(review.getComment());
        oldReview.setCreatedAt(review.getCreatedAt());
        oldReview.setRating(review.getRating());


        reviewRepository.save(oldReview);
    }

    public void deleteReview(Integer id) {

        Review review = reviewRepository.findReviewById(id);

        if (review == null) {
            throw new ApiException("Review not found");
        }

              reviewRepository.delete(review);
    }
}





// oldReview.setUser(review.getUser());
//oldReview.setEvent(review.getEvent());
// oldReview.setInitiative(review.getInitiative());