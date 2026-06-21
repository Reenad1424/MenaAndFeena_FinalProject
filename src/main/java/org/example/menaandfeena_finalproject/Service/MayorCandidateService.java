package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MayorCandidateInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.*;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class MayorCandidateService {

    private final MayorCandidateRepository mayorCandidateRepository;
    private final ElectionRoundRepository electionRoundRepository;
    private final UserRepository userRepository;
    private final MayorVoteRepository mayorVoteRepository;
    private final UserService userService;
    private final InitiativeParticipationRepository initiativeParticipationRepository;
    private final IssueReportRepository issueReportRepository;
    private final EventRegistrationRepository eventRegistrationRepository;


    //Reenad
    // =========================
    // GET ALL MAYOR CANDIDATES
    // =========================

    public List<MayorCandidateOutDTO> getAllMayorCandidates() {

        List<MayorCandidateOutDTO> mayorCandidateOutDTOS = new ArrayList<>();

        for (MayorCandidate mayorCandidate : mayorCandidateRepository.findAll()) {
            mayorCandidateOutDTOS.add(toOutDTO(mayorCandidate));
        }

        return mayorCandidateOutDTOS;
    }


    // =========================
    // APPLY FOR MAYOR CANDIDACY
    // =========================

    public void applyForMayorCandidacy(Integer userId, Integer roundId) {

        User user = getUserOrThrow(userId);

        ElectionRound round = getRoundOrThrow(roundId);

        validateCandidateNeighborhood(user, round);

        validateRoundIsActive(round);

        validateCandidateAge(user);

        validateUserNotAlreadyCandidate(userId, roundId);

        MayorCandidate candidate = new MayorCandidate();

        candidate.setUser(user);
        candidate.setElectionRound(round);
        candidate.setStatus("CANDIDATE");
        candidate.setAppliedAt(LocalDateTime.now());

        mayorCandidateRepository.save(candidate);
    }


    // =========================
    // GET ROUND CANDIDATES
    // =========================

    public List<CandidateResponseDto> getCandidatesByRound(Integer roundId) {

        ElectionRound round = getRoundOrThrow(roundId);

        if (round.getNeighborhood() == null) {
            throw new ApiException("Election round is not assigned to a neighborhood");
        }

        List<CandidateResponseDto> dtos = new ArrayList<>();

        List<MayorCandidate> candidates =
                mayorCandidateRepository.findByElectionRoundId(roundId);

        for (MayorCandidate candidate : candidates) {

            if (!isCandidateFromRoundNeighborhood(candidate, round)) {
                continue;
            }

            int voteCount =
                    mayorVoteRepository.countByMayorCandidateId(candidate.getId());

            CandidateResponseDto dto =
                    new CandidateResponseDto(
                            candidate.getId(),
                            candidate.getUser().getFullName(),
                            candidate.getUser().getGender(),
                            voteCount,
                            candidate.getStatus(),
                            "WINNER".equalsIgnoreCase(candidate.getStatus())
                    );

            dtos.add(dto);
        }

        return dtos;
    }


    // =========================
    // GET ELECTION DASHBOARD
    // =========================

    public ElectionPageDTO getElectionDashboard(Integer roundId) {

        ElectionRound round = getRoundOrThrow(roundId);

        List<CandidateResponseDto> candidates =
                getCandidatesByRound(roundId);

        int totalVotes = 0;

        for (CandidateResponseDto candidate : candidates) {
            totalVotes += candidate.getTotalVotes();
        }

        String neighborhoodName =
                round.getNeighborhood() != null
                        ? round.getNeighborhood().getName()
                        : "غير محدد";

        String message =
                "ACTIVE".equalsIgnoreCase(round.getStatus())
                        ? "الجولة نشطة حالياً ويمكن التصويت للمرشحين."
                        : "الجولة مغلقة ويمكن عرض النتائج النهائية.";

        return new ElectionPageDTO(
                round.getId(),
                neighborhoodName,
                round.getStatus(),
                message,
                candidates.size(),
                totalVotes,
                candidates
        );
    }


    // =========================
    // GET CANDIDATE PROFILE
    // =========================

    public CandidateDetailsDTO getCandidateProfile(Integer candidateId) {

        MayorCandidate candidate = getCandidateOrThrow(candidateId);

        User user = candidate.getUser();

        String neighborhoodName =
                user.getNeighborhood() != null
                        ? user.getNeighborhood().getName()
                        : "غير محدد";

        int joinedInitiativesCount =
                initiativeParticipationRepository.countByUserId(user.getId());

        int resolvedIssuesCount =
                issueReportRepository.countByReporterIdAndStatus(
                        user.getId(),
                        "COMPLETED"
                );

        int organizedEventsCount =
                eventRegistrationRepository.countByUserIdAndStatus(
                        user.getId(),
                        "CONFIRMED"
                );

        int registeredResidents =
                user.getNeighborhood() != null &&
                        user.getNeighborhood().getRegisteredPopulation() != null
                        ? user.getNeighborhood().getRegisteredPopulation()
                        : 0;

        int memberSinceYear =
                user.getCreatedAt() != null
                        ? user.getCreatedAt().getYear()
                        : LocalDate.now().getYear();

        int totalVotes =
                mayorVoteRepository.countByMayorCandidateId(candidate.getId());

        List<String> initiatives =
                getCandidateInitiatives(user.getId());

        List<String> events =
                getCandidateEvents(user.getId());

        return new CandidateDetailsDTO(
                candidate.getId(),
                user.getFullName(),
                neighborhoodName,
                memberSinceYear,
                candidate.getStatus(),
                candidate.getAppliedAt(),
                totalVotes,
                organizedEventsCount,
                joinedInitiativesCount,
                resolvedIssuesCount,
                registeredResidents,
                initiatives,
                events
        );
    }


    // =========================
    // UPDATE CANDIDATE
    // =========================

    public void updateMayorCandidate(Integer candidateId,
                                     MayorCandidateInDTO mayorCandidateInDTO) {

        MayorCandidate oldMayorCandidate =
                mayorCandidateRepository.findMayorCandidateById(candidateId);

        if (oldMayorCandidate == null) {
            throw new ApiException("Mayor candidate not found");
        }

        oldMayorCandidate.setAppliedAt(LocalDateTime.now());

        mayorCandidateRepository.save(oldMayorCandidate);
    }


    // =========================
    // DELETE CANDIDATE
    // =========================

    public void deleteMayorCandidate(Integer candidateId) {

        MayorCandidate mayorCandidate =
                mayorCandidateRepository.findMayorCandidateById(candidateId);

        if (mayorCandidate == null) {
            throw new ApiException("Mayor candidate not found");
        }

        mayorCandidateRepository.delete(mayorCandidate);
    }


    // =========================
    // PRIVATE HELPERS
    // =========================

    private User getUserOrThrow(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("Associated user not found");
        }

        if (user.getNeighborhood() == null) {
            throw new ApiException("User is not assigned to a neighborhood");
        }

        return user;
    }


    private ElectionRound getRoundOrThrow(Integer roundId) {

        ElectionRound round =
                electionRoundRepository.findById(roundId).orElse(null);

        if (round == null) {
            throw new ApiException("Election round not found");
        }

        if (round.getNeighborhood() == null) {
            throw new ApiException("Election round is not assigned to a neighborhood");
        }

        return round;
    }


    private MayorCandidate getCandidateOrThrow(Integer candidateId) {

        MayorCandidate candidate =
                mayorCandidateRepository.findById(candidateId).orElse(null);

        if (candidate == null) {
            throw new ApiException("Mayor candidate not found");
        }

        return candidate;
    }


    private void validateRoundIsActive(ElectionRound round) {

        if (!"ACTIVE".equalsIgnoreCase(round.getStatus())) {
            throw new ApiException("This election round is closed for nominations");
        }
    }


    private void validateCandidateAge(User user) {

        Integer age = userService.calculateAge(user.getBirthDate());

        if (age == null || age < 30) {
            throw new ApiException("عذراً، يجب ألا يقل عمر المرشح لمنصب العمدة عن 30 عاماً!");
        }
    }


    private void validateUserNotAlreadyCandidate(Integer userId,
                                                 Integer roundId) {

        boolean alreadyApplied =
                mayorCandidateRepository.existsByUserIdAndElectionRoundId(
                        userId,
                        roundId
                );

        if (alreadyApplied) {
            throw new ApiException("You have already applied as a candidate for this round");
        }
    }


    private void validateCandidateNeighborhood(User user,
                                               ElectionRound round) {

        if (!user.getNeighborhood().getId()
                .equals(round.getNeighborhood().getId())) {

            throw new ApiException("لا يمكنك الترشح في جولة انتخابية خارج حيّك");
        }
    }


    private boolean isCandidateFromRoundNeighborhood(MayorCandidate candidate,
                                                     ElectionRound round) {

        return candidate.getUser() != null
                && candidate.getUser().getNeighborhood() != null
                && candidate.getUser().getNeighborhood().getId()
                .equals(round.getNeighborhood().getId());
    }


    private List<String> getCandidateInitiatives(Integer userId) {

        List<String> initiatives = new ArrayList<>();

        List<InitiativeParticipation> participations =
                initiativeParticipationRepository.findByUserId(userId);

        for (InitiativeParticipation p : participations) {

            if (p.getInitiative() != null) {
                initiatives.add(p.getInitiative().getTitle());
            }
        }

        return initiatives;
    }


    private List<String> getCandidateEvents(Integer userId) {

        List<String> events = new ArrayList<>();

        List<EventRegistration> registrations =
                eventRegistrationRepository.findByUserId(userId);

        for (EventRegistration reg : registrations) {

            if (reg.getEvent() != null) {
                events.add(reg.getEvent().getTitle());
            }
        }

        return events;
    }


    private MayorCandidateOutDTO toOutDTO(MayorCandidate mayorCandidate) {

        return new MayorCandidateOutDTO(
                mayorCandidate.getId(),
                mayorCandidate.getAppliedAt(),
                mayorCandidate.getStatus()
        );
    }
}
