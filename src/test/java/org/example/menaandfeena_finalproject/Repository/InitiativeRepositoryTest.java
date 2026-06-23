package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InitiativeRepositoryTest {

    @Autowired
    private InitiativeRepository initiativeRepository;

    @Autowired
    private TestEntityManager entityManager;

    User user;
    Neighborhood neighborhood;
    Initiative initiative1;
    Initiative initiative2;

    @BeforeEach
    void setUp() {
        String unique = String.valueOf(System.currentTimeMillis());

        neighborhood = new Neighborhood();
        neighborhood.setName("Al Narjis");
        neighborhood.setCity("Riyadh");
        neighborhood.setLatitude(24.7136);
        neighborhood.setLongitude(46.6753);
        neighborhood.setEstimatedPopulation(5000);
        neighborhood.setRegisteredPopulation(100);
        entityManager.persist(neighborhood);

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
        user.setNeighborhood(neighborhood);
        entityManager.persist(user);

        initiative1 = new Initiative();
        initiative1.setTitle("Clean Neighborhood");
        initiative1.setDescription("Cleaning initiative");
        initiative1.setCategory("VOLUNTEERING");
        initiative1.setDate(LocalDate.now().plusDays(2));
        initiative1.setMaxParticipants(50);
        initiative1.setStatus("ACTIVE");
        initiative1.setUser(user);
        initiative1.setCreator(user);
        initiative1.setNeighborhood(neighborhood);
        entityManager.persist(initiative1);

        initiative2 = new Initiative();
        initiative2.setTitle("Plant Trees");
        initiative2.setDescription("Planting initiative");
        initiative2.setCategory("ENVIRONMENT");
        initiative2.setDate(LocalDate.now().plusDays(5));
        initiative2.setMaxParticipants(30);
        initiative2.setStatus("ACTIVE");
        initiative2.setUser(user);
        initiative2.setCreator(user);
        initiative2.setNeighborhood(neighborhood);
        entityManager.persist(initiative2);

        entityManager.flush();
    }

    @Test
    void findInitiativeById_ShouldReturnInitiative() {
        Initiative found = initiativeRepository.findInitiativeById(initiative1.getId());

        assertEquals(initiative1.getId(), found.getId());
    }

    @Test
    void findInitiativesByCategory_ShouldReturnInitiatives() {
        List<Initiative> initiatives =
                initiativeRepository.findInitiativesByCategory("VOLUNTEERING");

        assertEquals(1, initiatives.size());
    }

    @Test
    void findInitiativesByDateAfter_ShouldReturnUpcomingInitiatives() {
        List<Initiative> initiatives =
                initiativeRepository.findInitiativesByDateAfter(LocalDate.now());

        assertEquals(2, initiatives.size());
    }

    @Test
    void countByNeighborhoodAndStatus_ShouldReturnCount() {
        Integer count =
                initiativeRepository.countByNeighborhoodAndStatus(neighborhood, "ACTIVE");

        assertEquals(2, count);
    }

    @Test
    void countByStatus_ShouldReturnCount() {
        Integer count = initiativeRepository.countByStatus("ACTIVE");

        assertTrue(count >= 2);
    }

    @Test
    void findByCreatorId_ShouldReturnInitiatives() {
        List<Initiative> initiatives =
                initiativeRepository.findByCreatorId(user.getId());

        assertEquals(2, initiatives.size());
    }

    @Test
    void countByNeighborhood_Id_ShouldReturnCount() {
        Integer count =
                initiativeRepository.countByNeighborhood_Id(neighborhood.getId());

        assertEquals(2, count);
    }

    @Test
    void findByNeighborhood_Id_ShouldReturnInitiatives() {
        List<Initiative> initiatives =
                initiativeRepository.findByNeighborhood_Id(neighborhood.getId());

        assertEquals(2, initiatives.size());
    }
}