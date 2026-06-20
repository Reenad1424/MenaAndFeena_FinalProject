package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.ElectionRoundInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionRoundDetailsDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionRoundOutDTO;
import org.example.menaandfeena_finalproject.Model.ElectionRound;
import org.example.menaandfeena_finalproject.Model.MayorCandidate;
import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ElectionRoundService {

    private final ElectionRoundRepository electionRoundRepository;
    private final UserRepository userRepository;
    private final MayorCandidateRepository mayorCandidateRepository;
    private final MayorVoteRepository mayorVoteRepository;
    private final MayorProfileRepository mayorProfileRepository;
    private final EmailService emailService;

    //Reenad
    // =========================
    // GET ALL ELECTION ROUNDS
    // =========================

    public List<ElectionRoundOutDTO> getAllElectionRounds() {

        List<ElectionRoundOutDTO> electionRoundOutDTOS = new ArrayList<>();

        for (ElectionRound electionRound : electionRoundRepository.findAll()) {

            closeRoundAndSelectWinnerIfNeeded(electionRound);

            electionRoundOutDTOS.add(toOutDTO(electionRound));
        }

        return electionRoundOutDTOS;
    }


    // =========================
    // GET ELECTION ROUND DETAILS
    // =========================

    public ElectionRoundDetailsDTO getElectionRoundDetails(Integer roundId) {

        ElectionRound round = getRoundOrThrow(roundId);

        closeRoundAndSelectWinnerIfNeeded(round);

        openNewRoundIfMayorTermExpired(round);

        return buildElectionRoundDetails(round);
    }


    // =========================
    // CREATE ELECTION ROUND
    // =========================

    public void createElectionRound(ElectionRoundInDTO electionRoundInDTO) {

        ElectionRound electionRound = new ElectionRound();

        electionRound.setStartDate(electionRoundInDTO.getStartDate());
        electionRound.setEndDate(electionRoundInDTO.getEndDate());
        electionRound.setStatus("ACTIVE");

        electionRoundRepository.save(electionRound);
    }


    // =========================
    // UPDATE ELECTION ROUND
    // =========================

    public void updateElectionRound(Integer roundId,
                                    ElectionRoundInDTO electionRoundInDTO) {

        ElectionRound oldElectionRound = getRoundOrThrow(roundId);

        oldElectionRound.setStartDate(electionRoundInDTO.getStartDate());
        oldElectionRound.setEndDate(electionRoundInDTO.getEndDate());
        oldElectionRound.setStatus(electionRoundInDTO.getStatus());

        electionRoundRepository.save(oldElectionRound);
    }


    // =========================
    // DELETE ELECTION ROUND
    // =========================

    public void deleteElectionRound(Integer roundId) {

        ElectionRound electionRound = getRoundOrThrow(roundId);

        electionRoundRepository.delete(electionRound);
    }


    // =========================
    // PRIVATE HELPERS
    // =========================

    private ElectionRound getRoundOrThrow(Integer roundId) {

        ElectionRound round =
                electionRoundRepository.findElectionRoundById(roundId);

        if (round == null) {
            throw new ApiException("الجولة الانتخابية غير موجودة");
        }

        return round;
    }



    private void closeRoundAndSelectWinnerIfNeeded(ElectionRound round) {

        LocalDate today = LocalDate.now();

        if ("ACTIVE".equalsIgnoreCase(round.getStatus())) {

            if (today.isBefore(round.getEndDate())) {
                return;
            }

            round.setStatus("CLOSED");
            electionRoundRepository.save(round);
        }

        if (!"CLOSED".equalsIgnoreCase(round.getStatus())) {
            return;
        }

        List<MayorCandidate> candidates =
                mayorCandidateRepository.findByElectionRoundId(round.getId());

        if (candidates == null || candidates.isEmpty()) {
            return;
        }

        boolean alreadyHasWinner = false;

        for (MayorCandidate candidate : candidates) {
            if ("WINNER".equalsIgnoreCase(candidate.getStatus())) {
                alreadyHasWinner = true;
                break;
            }
        }

        if (alreadyHasWinner) {
            return;
        }

        MayorCandidate winnerCandidate = null;
        int maxVotes = -1;

        for (MayorCandidate candidate : candidates) {

            int voteCount =
                    mayorVoteRepository.countByMayorCandidateId(candidate.getId());

            if (voteCount > maxVotes) {
                maxVotes = voteCount;
                winnerCandidate = candidate;
            }
        }

        if (winnerCandidate == null) {
            return;
        }

        updateCandidateStatuses(candidates, winnerCandidate);

        assignMayor(winnerCandidate);
    }


    private void updateCandidateStatuses(List<MayorCandidate> candidates,
                                         MayorCandidate winnerCandidate) {

        for (MayorCandidate candidate : candidates) {

            if (candidate.getId().equals(winnerCandidate.getId())) {
                candidate.setStatus("WINNER");
            } else {
                candidate.setStatus("NOT_SELECTED");
            }

            mayorCandidateRepository.save(candidate);
        }
    }


    private void assignMayor(MayorCandidate winnerCandidate) {

        User winningUser = winnerCandidate.getUser();

        winningUser.setStatus("MAYOR");
        userRepository.save(winningUser);

        boolean hasActiveMayorProfile =
                mayorProfileRepository.existsByUserIdAndStatus(
                        winningUser.getId(),
                        "ACTIVE"
                );

        if (hasActiveMayorProfile) {
            return;
        }

        MayorProfile mayorProfile = new MayorProfile();

        mayorProfile.setUser(winningUser);
        mayorProfile.setStartDate(LocalDate.now());
        mayorProfile.setEndDate(LocalDate.now().plusYears(1));
        mayorProfile.setNeighborhood(winningUser.getNeighborhood());
        mayorProfile.setStatus("ACTIVE");

        MayorProfile savedMayorProfile =
                mayorProfileRepository.save(mayorProfile);

        int winnerVotes =
                mayorVoteRepository.countByMayorCandidateId(
                        winnerCandidate.getId()
                );

        emailService.sendMayorAppointmentEmail(
                winningUser,
                savedMayorProfile,
                winnerVotes
        );
    }


    private void openNewRoundIfMayorTermExpired(ElectionRound round) {

        if (round.getNeighborhood() == null) {
            return;
        }

        LocalDate today = LocalDate.now();

        MayorProfile currentMayorProfile =
                mayorProfileRepository
                        .findTopByNeighborhoodIdAndStatusOrderByStartDateDesc(
                                round.getNeighborhood().getId(),
                                "ACTIVE"
                        );

        if (currentMayorProfile == null) {
            return;
        }

        if (!today.isAfter(currentMayorProfile.getEndDate())) {
            return;
        }

        boolean hasActiveRoundNow =
                electionRoundRepository.existsByStatusAndNeighborhoodId(
                        "ACTIVE",
                        currentMayorProfile.getNeighborhood().getId()
                );

        if (hasActiveRoundNow) {
            return;
        }

        User oldMayor = currentMayorProfile.getUser();

        oldMayor.setStatus("RESIDENT");
        userRepository.save(oldMayor);

        currentMayorProfile.setStatus("INACTIVE");
        mayorProfileRepository.save(currentMayorProfile);

        ElectionRound nextRound = new ElectionRound();

        nextRound.setStartDate(today);
        nextRound.setEndDate(today.plusDays(1));
        nextRound.setStatus("ACTIVE");
        nextRound.setNeighborhood(currentMayorProfile.getNeighborhood());

        electionRoundRepository.save(nextRound);
    }


    private ElectionRoundDetailsDTO buildElectionRoundDetails(ElectionRound round) {

        String winnerName = null;
        Integer winnerVotes = null;

        List<MayorCandidate> roundCandidates =
                mayorCandidateRepository.findByElectionRoundId(round.getId());

        int totalCandidates = roundCandidates.size();

        int totalVotes = 0;

        for (MayorCandidate candidate : roundCandidates) {

            int candidateVotes =
                    mayorVoteRepository.countByMayorCandidateId(candidate.getId());

            totalVotes += candidateVotes;

            if ("WINNER".equalsIgnoreCase(candidate.getStatus())) {
                winnerName = candidate.getUser().getFullName();
                winnerVotes = candidateVotes;
            }
        }

        Long daysRemaining = 0L;

        LocalDate today = LocalDate.now();

        if ("ACTIVE".equalsIgnoreCase(round.getStatus())
                && !today.isAfter(round.getEndDate())) {

            daysRemaining =
                    java.time.temporal.ChronoUnit.DAYS.between(
                            today,
                            round.getEndDate()
                    );
        }

        String statusDescription =
                "ACTIVE".equalsIgnoreCase(round.getStatus())
                        ? "نشطة ومتاحة للتصويت والترشح"
                        : "مغلقة ومنتهية";

        String message =
                "ACTIVE".equalsIgnoreCase(round.getStatus())
                        ? "الجولة نشطة حالياً، ويمكن للسكان التصويت والترشح حتى تاريخ الإغلاق."
                        : "تم إغلاق الجولة الانتخابية وانتهت فترة التصويت.";

        String neighborhoodName =
                round.getNeighborhood() != null
                        ? round.getNeighborhood().getName()
                        : "غير محدد";

        return new ElectionRoundDetailsDTO(
                round.getId(),
                neighborhoodName,
                round.getStartDate(),
                round.getEndDate(),
                round.getStatus(),
                statusDescription,
                daysRemaining,
                totalCandidates,
                totalVotes,
                message,
                winnerName,
                winnerVotes
        );
    }


    private ElectionRoundOutDTO toOutDTO(ElectionRound electionRound) {

        return new ElectionRoundOutDTO(
                electionRound.getId(),
                electionRound.getStartDate(),
                electionRound.getEndDate(),
                electionRound.getStatus()
        );
    }
}