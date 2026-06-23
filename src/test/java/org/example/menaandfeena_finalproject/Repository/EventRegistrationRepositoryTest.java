package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventRegistrationRepositoryTest {

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private TestEntityManager entityManager;

    User user;
    FamilyMember familyMember;
    Event event;

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

        familyMember = new FamilyMember();
        familyMember.setName("Ahmed");
        familyMember.setAge(10);
        familyMember.setRelation("son");
        familyMember.setGender("MALE");
        familyMember.setUser(user);
        entityManager.persist(familyMember);

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

        EventRegistration registration1 = new EventRegistration();
        registration1.setUser(user);
        registration1.setEvent(event);
        registration1.setFamilyMember(familyMember);
        registration1.setStatus("CONFIRMED");
        registration1.setRegisteredAt(LocalDate.now());
        entityManager.persist(registration1);

        entityManager.flush();
    }

    @Test
    void existsByUserIdAndEventId_ShouldReturnTrue() {
        boolean exists = eventRegistrationRepository
                .existsByUserIdAndEventId(user.getId(), event.getId());

        assertTrue(exists);
    }

    @Test
    void existsByUserIdAndEventIdAndStatus_ShouldReturnTrue() {
        boolean exists = eventRegistrationRepository
                .existsByUserIdAndEventIdAndStatus(
                        user.getId(),
                        event.getId(),
                        "CONFIRMED"
                );

        assertTrue(exists);
    }

    @Test
    void existsByFamilyMemberIdAndEventId_ShouldReturnTrue() {
        boolean exists = eventRegistrationRepository
                .existsByFamilyMemberIdAndEventId(
                        familyMember.getId(),
                        event.getId()
                );

        assertTrue(exists);
    }

    @Test
    void countByEventId_ShouldReturnCount() {
        int count = eventRegistrationRepository.countByEventId(event.getId());

        assertEquals(1, count);
    }

    @Test
    void findByEventId_ShouldReturnRegistrations() {
        List<EventRegistration> registrations =
                eventRegistrationRepository.findByEventId(event.getId());

        assertEquals(1, registrations.size());
    }

    @Test
    void countByUserIdAndStatus_ShouldReturnCount() {
        int count = eventRegistrationRepository
                .countByUserIdAndStatus(user.getId(), "CONFIRMED");

        assertEquals(1, count);
    }

    @Test
    void findByUserId_ShouldReturnRegistrations() {
        List<EventRegistration> registrations =
                eventRegistrationRepository.findByUserId(user.getId());

        assertEquals(1, registrations.size());
    }
}