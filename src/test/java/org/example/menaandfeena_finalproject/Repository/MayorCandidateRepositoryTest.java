package org.example.menaandfeena_finalproject.Repository;

import org.assertj.core.api.Assertions;
import org.example.menaandfeena_finalproject.Model.ElectionRound;
import org.example.menaandfeena_finalproject.Model.MayorCandidate;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MayorCandidateRepositoryTest {

    @Autowired
    MayorCandidateRepository mayorCandidateRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ElectionRoundRepository electionRoundRepository;

    @Autowired
    NeighborhoodRepository neighborhoodRepository;

    Neighborhood neighborhood;
    User user;
    ElectionRound electionRound;
    MayorCandidate candidate;

    @BeforeEach
    void setUp() {
        neighborhood = new Neighborhood();
        neighborhood.setName("النرجس");
        neighborhood.setCity("الرياض");
        neighborhood.setRegisteredPopulation(3);

        user = new User();
        user.setFullName("Reenad");
        user.setEmail("reenad@test.com");
        user.setPassword("123456");
        user.setPhone("0500000000");
        user.setNationalId("1234567890");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender("FEMALE");
        user.setStatus("RESIDENT");
        user.setNeighborhood(neighborhood);

        electionRound = new ElectionRound();
        electionRound.setStartDate(LocalDate.now());
        electionRound.setEndDate(LocalDate.now().plusDays(1));
        electionRound.setStatus("ACTIVE");
        electionRound.setNeighborhood(neighborhood);

        candidate = new MayorCandidate();
        candidate.setAppliedAt(LocalDateTime.now());
        candidate.setStatus("CANDIDATE");
        candidate.setUser(user);
        candidate.setElectionRound(electionRound);
    }

    @Test
    public void findMayorCandidateByIdTest() {
        neighborhoodRepository.save(neighborhood);
        userRepository.save(user);
        electionRoundRepository.save(electionRound);
        mayorCandidateRepository.save(candidate);

        MayorCandidate found = mayorCandidateRepository.findMayorCandidateById(candidate.getId());

        Assertions.assertThat(found).isEqualTo(candidate);
    }

    @Test
    public void findByElectionRoundIdTest() {
        neighborhoodRepository.save(neighborhood);
        userRepository.save(user);
        electionRoundRepository.save(electionRound);
        mayorCandidateRepository.save(candidate);

        List<MayorCandidate> result = mayorCandidateRepository.findByElectionRoundId(electionRound.getId());

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0).getStatus()).isEqualTo("CANDIDATE");
    }

    @Test
    public void existsByUserIdAndElectionRoundIdTest() {
        neighborhoodRepository.save(neighborhood);
        userRepository.save(user);
        electionRoundRepository.save(electionRound);
        mayorCandidateRepository.save(candidate);

        boolean exists = mayorCandidateRepository.existsByUserIdAndElectionRoundId(
                user.getId(),
                electionRound.getId()
        );

        Assertions.assertThat(exists).isTrue();
    }
}