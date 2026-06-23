package org.example.menaandfeena_finalproject.Service;

import org.example.menaandfeena_finalproject.DTO.Out.CandidateDetailsDTO;
import org.example.menaandfeena_finalproject.DTO.Out.CandidateResponseDto;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionPageDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MayorCandidateOutDTO;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MayorCandidateServiceTest {

    @InjectMocks
    MayorCandidateService mayorCandidateService;

    @Mock
    MayorCandidateRepository mayorCandidateRepository;
    @Mock
    ElectionRoundRepository electionRoundRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    MayorVoteRepository mayorVoteRepository;
    @Mock
    UserService userService;
    @Mock
    InitiativeParticipationRepository initiativeParticipationRepository;
    @Mock
    IssueReportRepository issueReportRepository;
    @Mock
    EventRegistrationRepository eventRegistrationRepository;

    User user;
    Neighborhood neighborhood;
    ElectionRound round;
    MayorCandidate candidate;
    List<MayorCandidate> candidates;

    @BeforeEach
    void setUp() {
        neighborhood = new Neighborhood();
        neighborhood.setId(1);
        neighborhood.setName("النرجس");
        neighborhood.setCity("الرياض");
        neighborhood.setRegisteredPopulation(10);

        user = new User();
        user.setId(1);
        user.setFullName("Reenad");
        user.setGender("FEMALE");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCreatedAt(LocalDate.now().minusYears(2));
        user.setNeighborhood(neighborhood);

        round = new ElectionRound();
        round.setId(1);
        round.setStatus("ACTIVE");
        round.setStartDate(LocalDate.now());
        round.setEndDate(LocalDate.now().plusDays(1));
        round.setNeighborhood(neighborhood);

        candidate = new MayorCandidate();
        candidate.setId(1);
        candidate.setAppliedAt(LocalDateTime.now());
        candidate.setStatus("CANDIDATE");
        candidate.setUser(user);
        candidate.setElectionRound(round);

        candidates = new ArrayList<>();
        candidates.add(candidate);
    }

    @Test
    public void getAllMayorCandidatesTest() {
        when(mayorCandidateRepository.findAll()).thenReturn(candidates);

        List<MayorCandidateOutDTO> result = mayorCandidateService.getAllMayorCandidates();

        Assertions.assertEquals(1, result.size());

        verify(mayorCandidateRepository, times(1)).findAll();
    }

    @Test
    public void applyForMayorCandidacyTest() {
        when(userRepository.findUserById(user.getId())).thenReturn(user);
        when(electionRoundRepository.findById(round.getId())).thenReturn(Optional.of(round));
        when(userService.calculateAge(user.getBirthDate())).thenReturn(35);
        when(mayorCandidateRepository.existsByUserIdAndElectionRoundId(user.getId(), round.getId())).thenReturn(false);

        mayorCandidateService.applyForMayorCandidacy(user.getId(), round.getId());

        verify(userRepository, times(1)).findUserById(user.getId());
        verify(electionRoundRepository, times(1)).findById(round.getId());
        verify(mayorCandidateRepository, times(1)).save(any(MayorCandidate.class));
    }

    @Test
    public void getCandidatesByRoundTest() {
        when(electionRoundRepository.findById(round.getId())).thenReturn(Optional.of(round));
        when(mayorCandidateRepository.findByElectionRoundId(round.getId())).thenReturn(candidates);
        when(mayorVoteRepository.countByMayorCandidateId(candidate.getId())).thenReturn(3);

        List<CandidateResponseDto> result = mayorCandidateService.getCandidatesByRound(round.getId());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(3, result.get(0).getTotalVotes());

        verify(electionRoundRepository, times(1)).findById(round.getId());
        verify(mayorCandidateRepository, times(1)).findByElectionRoundId(round.getId());
        verify(mayorVoteRepository, times(1)).countByMayorCandidateId(candidate.getId());
    }

    @Test
    public void getElectionDashboardTest() {
        when(electionRoundRepository.findById(round.getId())).thenReturn(Optional.of(round));
        when(mayorCandidateRepository.findByElectionRoundId(round.getId())).thenReturn(candidates);
        when(mayorVoteRepository.countByMayorCandidateId(candidate.getId())).thenReturn(5);

        ElectionPageDTO result = mayorCandidateService.getElectionDashboard(round.getId());

        Assertions.assertEquals(1, result.getTotalCandidates());
        Assertions.assertEquals(5, result.getTotalVotes());

        verify(electionRoundRepository, times(2)).findById(round.getId());
    }

    @Test
    public void getCandidateProfileTest() {
        Initiative initiative = new Initiative();
        initiative.setTitle("مبادرة التشجير");

        InitiativeParticipation participation = new InitiativeParticipation();
        participation.setInitiative(initiative);

        Event event = new Event();
        event.setTitle("فعالية الحي");

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);

        when(mayorCandidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(initiativeParticipationRepository.countByUserId(user.getId())).thenReturn(1);
        when(issueReportRepository.countByReporterIdAndStatus(user.getId(), "COMPLETED")).thenReturn(2);
        when(eventRegistrationRepository.countByUserIdAndStatus(user.getId(), "CONFIRMED")).thenReturn(1);
        when(mayorVoteRepository.countByMayorCandidateId(candidate.getId())).thenReturn(4);
        when(initiativeParticipationRepository.findByUserId(user.getId())).thenReturn(List.of(participation));
        when(eventRegistrationRepository.findByUserId(user.getId())).thenReturn(List.of(registration));

        CandidateDetailsDTO result = mayorCandidateService.getCandidateProfile(candidate.getId());

        Assertions.assertEquals(candidate.getId(), result.getCandidateId());
        Assertions.assertEquals("Reenad", result.getFullName());
        Assertions.assertEquals(4, result.getTotalVotes());

        verify(mayorCandidateRepository, times(1)).findById(candidate.getId());
    }

    @Test
    public void updateMayorCandidateTest() {
        when(mayorCandidateRepository.findMayorCandidateById(candidate.getId())).thenReturn(candidate);

        mayorCandidateService.updateMayorCandidate(candidate.getId());

        verify(mayorCandidateRepository, times(1)).findMayorCandidateById(candidate.getId());
        verify(mayorCandidateRepository, times(1)).save(candidate);
    }

    @Test
    public void deleteMayorCandidateTest() {
        when(mayorCandidateRepository.findMayorCandidateById(candidate.getId())).thenReturn(candidate);

        mayorCandidateService.deleteMayorCandidate(candidate.getId());

        verify(mayorCandidateRepository, times(1)).findMayorCandidateById(candidate.getId());
        verify(mayorCandidateRepository, times(1)).delete(candidate);
    }
}