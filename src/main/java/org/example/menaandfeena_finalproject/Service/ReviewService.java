package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.Review;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.EventRepository;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.example.menaandfeena_finalproject.Repository.ReviewRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
  private final InitiativeRepository initiativeRepository;


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



// Walaa

    public void addEventReview(Integer userId, Integer eventId, Review review) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        Event event = eventRepository.findEventById(eventId);

        if (event == null) {
            throw new ApiException("Event not found");
        }

        review.setUser(user);
        review.setEvent(event);
        review.setCreatedAt(LocalDate.now());

        reviewRepository.save(review);
    }


// Walaa
public List<Review> getEventReviews(Integer eventId) {
    Event event = eventRepository.findEventById(eventId);
    if (event == null) {
        throw new ApiException("Event not found");
    }
    return reviewRepository.findByEvent_Id(eventId);
}


// Walaa
public List<Review> getInitiativeReviews(Integer initiativeId) {

    Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);
    if (initiative == null) {
        throw new ApiException("Initiative not found");
    }
    return reviewRepository.findByInitiative_Id(initiativeId);

}

// Walaa





}

// oldReview.setUser(review.getUser());
//oldReview.setEvent(review.getEvent());
// oldReview.setInitiative(review.getInitiative());


