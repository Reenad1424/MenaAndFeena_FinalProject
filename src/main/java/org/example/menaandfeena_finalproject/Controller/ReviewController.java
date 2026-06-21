package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.ReviewInDTO;
import org.example.menaandfeena_finalproject.Service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.status(200).body(reviewService.getAllReviews());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@Valid @RequestBody ReviewInDTO reviewInDTO) {

        reviewService.addReview(reviewInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Review added successfully"));

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Integer id, @Valid @RequestBody ReviewInDTO reviewInDTO) {

        reviewService.updateReview(id, reviewInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Review updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(200).body(new ApiResponse("Review deleted successfully"));

    }


    @PostMapping("/add-event-review/{userId}/{eventId}")
    public ResponseEntity<?> addEventReview(@PathVariable Integer userId, @PathVariable Integer eventId,
                                         @Valid @RequestBody ReviewInDTO reviewInDTO) {
        reviewService.addEventReview(userId, eventId, reviewInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Event review added successfully"));
    }

    @PostMapping("/add-initiative-review/{userId}/{initiativeId}")
    public ResponseEntity<?> addInitiativeReview(@PathVariable Integer userId, @PathVariable Integer initiativeId,
                                              @Valid @RequestBody ReviewInDTO reviewInDTO) {
        reviewService.addInitiativeReview(userId, initiativeId, reviewInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Initiative review added successfully"));
    }


    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getEventReviews(@PathVariable Integer eventId) {

        return ResponseEntity.status(200).body(reviewService.getEventReviews(eventId));
    }


    @GetMapping("/initiative/{initiativeId}")
    public ResponseEntity<?> getInitiativeReviews(@PathVariable Integer initiativeId) {
        return ResponseEntity.status(200).body(reviewService.getInitiativeReviews(initiativeId));
    }




    @GetMapping("/event/{eventId}/filter")
    public ResponseEntity<?> getEventReviews(@PathVariable Integer eventId, @RequestParam(required = false) String sort) {

        if ("newest".equalsIgnoreCase(sort)) {
            return ResponseEntity.status(200).body(reviewService.getEventReviewsNewest(eventId));
        }
        return ResponseEntity.status(200).body(reviewService.getEventReviews(eventId));
    }



    @GetMapping("/average/{eventId}")
    public ResponseEntity<?> getAverageRating(@PathVariable Integer eventId) {
        return ResponseEntity.status(200).body(reviewService.getAverageRating(eventId));
    }


    @GetMapping("/positive-ratio/{eventId}")
    public ResponseEntity<?> getPositiveRatio(@PathVariable Integer eventId) {
        return ResponseEntity.status(200).body(reviewService.getPositiveRatio(eventId));
    }




    @GetMapping("/ai-summary/{eventId}")
    public ResponseEntity<?> getEventAISummary(@PathVariable Integer eventId) {

        return ResponseEntity.status(200)
                .body(reviewService.getEventAISummary(eventId));
    }



    @GetMapping("/average/initiative/{initiativeId}")
    public ResponseEntity<?> getAverageRatingByInitiative(@PathVariable Integer initiativeId) {
        return ResponseEntity.status(200).body(reviewService.getAverageRatingByInitiative(initiativeId));
    }


    @GetMapping("/positive-ratio/initiative/{initiativeId}")
    public ResponseEntity<?> getPositiveRatioByInitiative(@PathVariable Integer initiativeId) {
        return ResponseEntity.status(200).body(reviewService.getPositiveRatioByInitiative(initiativeId));
    }



}
