package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.Review;
import org.example.menaandfeena_finalproject.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    User user;
    Event event;
    Initiative initiative;

    @BeforeEach
    void setUp() {
        String unique = String.valueOf(System.currentTimeMillis());
        user = new User();
        user.setFullName("Walaa");
        user.setEmail("walaa" + unique + "@test.com");
        user.setPassword("123456");
        user.setPhone("9665" + unique.substring(unique.length() - 8));
        user.setLatitude(24.7136);
        user.setLongitude(46.6753);
        user.setBirthDate(LocalDate.of(1996, 12, 1));
        user.setGender("FEMALE");
        user.setNationalId(unique);
        entityManager.persist(user);


        event = new Event();
        event.setTitle("Drawing Event");
        event.setDescription("Art event");
        event.setDate(LocalDateTime.now().plusDays(1));
        event.setLocation("Riyadh");
        event.setIsPaid(false);
        event.setPrice(0.0);
        event.setMaxParticipants(20);
        event.setStatus("ACTIVE");
        event.setUser(user);
        entityManager.persist(event);

        initiative = new Initiative();
        initiative.setTitle("Clean Neighborhood");
        initiative.setDescription("Cleaning initiative");
        initiative.setCategory("VOLUNTEERING");
        initiative.setStatus("ACTIVE");
        initiative.setDate(LocalDate.now().plusDays(3));
        initiative.setMaxParticipants(50);
        initiative.setUser(user);
        entityManager.persist(initiative);

        Review review1 = new Review();
        review1.setComment("Excellent event");
        review1.setRating(5);
        review1.setCreatedAt(LocalDate.now());
        review1.setUser(user);
        review1.setEvent(event);
        entityManager.persist(review1);

        Review review2 = new Review();
        review2.setComment("Good event");
        review2.setRating(4);
        review2.setCreatedAt(LocalDate.now().minusDays(1));
        review2.setUser(user);
        review2.setEvent(event);
        entityManager.persist(review2);

        Review initiativeReview = new Review();
        initiativeReview.setComment("Great initiative");
        initiativeReview.setRating(5);
        initiativeReview.setCreatedAt(LocalDate.now());
        initiativeReview.setUser(user);
        initiativeReview.setInitiative(initiative);
        entityManager.persist(initiativeReview);

        entityManager.flush();
    }

    @Test
    void findByEvent_Id_ShouldReturnEventReviews() {
        List<Review> reviews = reviewRepository.findByEvent_Id(event.getId());

        assertEquals(2, reviews.size());
    }

    @Test
    void existsByUserIdAndEventId_ShouldReturnTrue() {
        boolean exists = reviewRepository.existsByUserIdAndEventId(user.getId(), event.getId());

        assertTrue(exists);
    }

    @Test
    void getAverageRatingByEventId_ShouldReturnAverage() {
        Double average = reviewRepository.getAverageRatingByEventId(event.getId());

        assertEquals(4.5, average);
    }

    @Test
    void countByEvent_Id_ShouldReturnReviewCount() {
        Integer count = reviewRepository.countByEvent_Id(event.getId());

        assertEquals(2, count);
    }

    @Test
    void countByEvent_IdAndRatingGreaterThanEqual_ShouldReturnPositiveCount() {
        Integer count = reviewRepository.countByEvent_IdAndRatingGreaterThanEqual(event.getId(), 4);

        assertEquals(2, count);
    }

    @Test
    void findByInitiative_Id_ShouldReturnInitiativeReviews() {
        List<Review> reviews = reviewRepository.findByInitiative_Id(initiative.getId());

        assertEquals(1, reviews.size());
    }
}