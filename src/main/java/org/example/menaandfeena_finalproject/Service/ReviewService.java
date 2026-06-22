package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.ReviewInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ReviewOutDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.Review;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.EventRepository;
import org.example.menaandfeena_finalproject.Repository.EventRegistrationRepository;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.example.menaandfeena_finalproject.Repository.InitiativeParticipationRepository;
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
    private final EventRegistrationRepository eventRegistrationRepository;
    private final InitiativeParticipationRepository initiativeParticipationRepository;
    private final OpenAIService openAIService;


    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public void addReview(ReviewInDTO reviewInDTO) {
        Review review = new Review();
        review.setComment(reviewInDTO.getComment());
        review.setRating(reviewInDTO.getRating());
        review.setCreatedAt(LocalDate.now());
        reviewRepository.save(review);
    }

    public void updateReview(Integer id, ReviewInDTO reviewInDTO) {

        Review oldReview = reviewRepository.findReviewById(id);

        if (oldReview == null) {
            throw new ApiException("Review not found");
        }

        oldReview.setComment(reviewInDTO.getComment());
        oldReview.setRating(reviewInDTO.getRating());


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

    public void addEventReview(Integer userId, Integer eventId, ReviewInDTO reviewInDTO) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }
        System.out.println("JWT USER ID = " + userId);
        Event event = eventRepository.findEventById(eventId);

        if (event == null) {
            throw new ApiException("Event not found");
        }
        if (!eventRegistrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new ApiException("User must register for the event before reviewing");
        }
        if (!eventRegistrationRepository.existsByUserIdAndEventIdAndStatus(userId, eventId, "CONFIRMED")) {
            throw new ApiException("User registration must be confirmed before reviewing");
        }
        if (reviewRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new ApiException("User already reviewed this event");
        }
        Review review = new Review();
        review.setComment(reviewInDTO.getComment());
        review.setRating(reviewInDTO.getRating());
        review.setCreatedAt(LocalDate.now());
        review.setUser(user);
        review.setEvent(event);
        reviewRepository.save(review);

    }

    public void addInitiativeReview(Integer userId, Integer initiativeId, ReviewInDTO reviewInDTO) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);

        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }
        if (!initiativeParticipationRepository.existsByUserIdAndInitiativeId(userId, initiativeId)) {
            throw new ApiException("User must join the initiative before reviewing");
        }
        if (reviewRepository.existsByUserIdAndInitiativeId(userId, initiativeId)) {
            throw new ApiException("User already reviewed this initiative");
        }

        Review review = new Review();
        review.setComment(reviewInDTO.getComment());
        review.setRating(reviewInDTO.getRating());
        review.setCreatedAt(LocalDate.now());
        review.setUser(user);
        review.setInitiative(initiative);
        reviewRepository.save(review);
    }


// Walaa
public List<ReviewOutDTO> getEventReviews(Integer eventId) {

    Event event = eventRepository.findEventById(eventId);

    if (event == null) {
        throw new ApiException("Event not found");
    }

    return reviewRepository.findByEvent_Id(eventId)
            .stream()
            .map(this::convertToOutDTO)
            .toList();
}


// Walaa
public List<ReviewOutDTO> getInitiativeReviews(Integer initiativeId) {

    Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);
    if (initiative == null) {
        throw new ApiException("Initiative not found");
    }
    return reviewRepository.findByInitiative_Id(initiativeId)
            .stream()
            .map(this::convertToOutDTO)
            .toList();

}

// Walaa

//    public List<ReviewOutDTO> getEventReviewsNewest(Integer eventId) {
//
//        Event event = eventRepository.findEventById(eventId);
//
//        if (event == null) {
//            throw new ApiException("Event not found");
//        }
//
//        return reviewRepository
//                .findByEvent_IdOrderByCreatedAtDesc(eventId)
//                .stream()
//                .map(this::convertToOutDTO)
//                .toList();
//    }


    //Walaa

    public List<ReviewOutDTO> getEventReviewsNewest(Integer eventId) {

        Event event = eventRepository.findEventById(eventId);

        if (event == null) {
            throw new ApiException("Event not found");
        }

        return reviewRepository.findByEvent_IdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(this::convertToOutDTO)
                .toList();
    }

// Walaa
   public Double getAverageRating(Integer eventId) {

    Event event = eventRepository.findEventById(eventId);

    if (event == null) {
        throw new ApiException("Event not found");
    }

    return reviewRepository.getAverageRatingByEventId(eventId);
}

// Walaa
    public Double getPositiveRatio(Integer eventId) {

        Event event = eventRepository.findEventById(eventId);

        if (event == null) {
            throw new ApiException("Event not found");
        }

        Integer totalReviews = reviewRepository.countByEvent_Id(eventId);
        if (totalReviews == 0) {
            return 0.0;
        }
        Integer positiveReviews = reviewRepository.countByEvent_IdAndRatingGreaterThanEqual(eventId,4);
        return (positiveReviews * 100.0) / totalReviews;

    }



// Walaa
public Double getAverageRatingByInitiative(Integer initiativeId) {
    Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);
    if (initiative == null) {
        throw new ApiException("Initiative not found");
    }
    return reviewRepository.getAverageRatingByInitiativeId(initiativeId);

}

// Walaa

    public Double getPositiveRatioByInitiative(Integer initiativeId) {

        Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);

        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }
        Integer totalReviews = reviewRepository.countByInitiative_Id(initiativeId);

        if (totalReviews == 0) {
            return 0.0;
        }
        Integer positiveReviews = reviewRepository.countByInitiative_IdAndRatingGreaterThanEqual(initiativeId, 4);

        return (positiveReviews * 100.0) / totalReviews;

    }



    public String getEventAISummary(Integer eventId) {

        Event event = eventRepository.findEventById(eventId);

        if (event == null) {
            throw new ApiException("Event not found");
        }

        List<Review> reviews = reviewRepository.findByEvent_Id(eventId);

        Double averageRating = reviewRepository.getAverageRatingByEventId(eventId);

        if (averageRating == null) {
            averageRating = 0.0;
        }

        if (reviews.isEmpty()) {
            throw new ApiException("No reviews found for this event");
        }
        StringBuilder reviewText = new StringBuilder();

        for (Review review : reviews) {

            reviewText.append("Rating: ")
                    .append(review.getRating())
                    .append("/5")
                    .append(" | Comment: ")
                    .append(review.getComment())
                    .append("\n");
        }
        String prompt =
                "Analyze the following event reviews and provide:\n" +
                        "1. Average satisfaction summary\n" +
                        "2. Positive feedback trend\n" +
                        "3. Most common complaint\n" +
                        "4. One recommendation for improvement\n\n" +
                        reviewText;

        String aiSummary = openAIService.askAI(
                """
                You are an AI review analyst for a smart neighborhood platform.
        
                        Analyze the reviews and return ONLY this format:
                        
                        😊 Positive Trend:
                           [one short sentence]
                        
                           ⚠ Common Complaint:
                            [one short sentence]
                        
                          💡 Recommendation:
                            [one short sentence]
                        
                            Do not mention rating scores.
                            Do not add extra explanations.
                            Keep the response concise.
        
                Keep the response concise and dashboard-friendly.
                """,
                prompt
        );

        return "⭐ Average Rating:\n"
                + String.format("%.1f/5", averageRating)
                + "\n\n"
                + aiSummary;
    }




// Walaa

    private ReviewOutDTO convertToOutDTO(Review review) {
        return new ReviewOutDTO(
                review.getId(),
                review.getComment(),
                review.getCreatedAt(),
                review.getRating()
        );
    }






}

// oldReview.setUser(review.getUser());
//oldReview.setEvent(review.getEvent());
// oldReview.setInitiative(review.getInitiative());


