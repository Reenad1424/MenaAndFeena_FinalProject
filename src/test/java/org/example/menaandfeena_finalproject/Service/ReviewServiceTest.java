package org.example.menaandfeena_finalproject.Service;

import org.example.menaandfeena_finalproject.DTO.In.ReviewInDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private InitiativeRepository initiativeRepository;

    @Mock
    private EventRegistrationRepository eventRegistrationRepository;

    @Mock
    private InitiativeParticipationRepository initiativeParticipationRepository;

    @Mock
    private OpenAIService openAIService;

    @InjectMocks
    private ReviewService reviewService;

    User user;
    Event event;
    ReviewInDTO reviewInDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFullName("Walaa");

        event = new Event();
        event.setId(1);
        event.setTitle("Drawing Event");

        reviewInDTO = new ReviewInDTO();
        reviewInDTO.setComment("Great event");
        reviewInDTO.setRating(5);
    }

    @Test
    void addEventReview_ShouldAddReviewSuccessfully() {
        when(userRepository.findUserById(1)).thenReturn(user);
        when(eventRepository.findEventById(1)).thenReturn(event);
        when(eventRegistrationRepository.existsByUserIdAndEventId(1, 1)).thenReturn(true);
        when(eventRegistrationRepository.existsByUserIdAndEventIdAndStatus(1, 1, "CONFIRMED")).thenReturn(true);
        when(reviewRepository.existsByUserIdAndEventId(1, 1)).thenReturn(false);

        reviewService.addEventReview(1, 1, reviewInDTO);

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addEventReview_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findUserById(1)).thenReturn(null);

        assertThrows(ApiException.class, () ->
                reviewService.addEventReview(1, 1, reviewInDTO)
        );
    }

    @Test
    void addEventReview_ShouldThrowException_WhenUserNotRegistered() {
        when(userRepository.findUserById(1)).thenReturn(user);
        when(eventRepository.findEventById(1)).thenReturn(event);
        when(eventRegistrationRepository.existsByUserIdAndEventId(1, 1)).thenReturn(false);

        assertThrows(ApiException.class, () ->
                reviewService.addEventReview(1, 1, reviewInDTO)
        );
    }

    @Test
    void getPositiveRatio_ShouldReturnCorrectRatio() {
        when(eventRepository.findEventById(1)).thenReturn(event);
        when(reviewRepository.countByEvent_Id(1)).thenReturn(4);
        when(reviewRepository.countByEvent_IdAndRatingGreaterThanEqual(1, 4)).thenReturn(3);

        Double result = reviewService.getPositiveRatio(1);

        assertEquals(75.0, result);
    }

    @Test
    void getAverageRating_ShouldReturnAverage() {
        when(eventRepository.findEventById(1)).thenReturn(event);
        when(reviewRepository.getAverageRatingByEventId(1)).thenReturn(4.5);

        Double result = reviewService.getAverageRating(1);

        assertEquals(4.5, result);
    }
}