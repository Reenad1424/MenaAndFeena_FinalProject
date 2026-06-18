package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Review;
import org.example.menaandfeena_finalproject.Service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/get")
    public ResponseEntity getAllReviews() {
        return ResponseEntity.status(200).body(reviewService.getAllReviews());
    }

    @PostMapping("/add")
    public ResponseEntity addReview(
            @Valid @RequestBody Review review) {

        reviewService.addReview(review);

        return ResponseEntity.status(200).body(new ApiResponse("Review added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateReview(@PathVariable Integer id, @Valid @RequestBody Review review) {

        reviewService.updateReview(id, review);

        return ResponseEntity.status(200).body(new ApiResponse("Review updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteReview(@PathVariable Integer id) {

        reviewService.deleteReview(id);

        return ResponseEntity.status(200).body(new ApiResponse("Review deleted successfully"));
    }


    @PostMapping("/add-event-review/{userId}/{eventId}")
    public ResponseEntity addEventReview(@PathVariable Integer userId, @PathVariable Integer eventId,
                                         @Valid @RequestBody Review review) {
        reviewService.addEventReview(userId, eventId, review);
        return ResponseEntity.status(200).body(new ApiResponse("Event review added successfully"));
    }


    @GetMapping("/event/{eventId}")
    public ResponseEntity getEventReviews(@PathVariable Integer eventId) {

        return ResponseEntity.status(200).body(reviewService.getEventReviews(eventId));
    }


    @GetMapping("/initiative/{initiativeId}")
    public ResponseEntity getInitiativeReviews(@PathVariable Integer initiativeId) {
        return ResponseEntity.status(200).body(reviewService.getInitiativeReviews(initiativeId));
    }





}
