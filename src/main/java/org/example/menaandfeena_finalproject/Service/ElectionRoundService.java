package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.ElectionRoundInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionRoundDetailsDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionRoundOutDTO;
import org.example.menaandfeena_finalproject.Model.ElectionRound;
import org.example.menaandfeena_finalproject.Model.MayorCandidate;
import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ElectionRoundService {

    private static final Logger log = LoggerFactory.getLogger(ElectionRoundService.class);

    private final ElectionRoundRepository electionRoundRepository;
    private final UserRepository userRepository;
    private final MayorCandidateRepository mayorCandidateRepository;
    private final MayorVoteRepository mayorVoteRepository;
    private final MayorProfileRepository mayorProfileRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

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
        if (!electionRoundInDTO.getEndDate().isAfter(electionRoundInDTO.getStartDate())) {
            throw new ApiException("End date must be after start date");
        }

        Neighborhood neighborhood = neighborhoodRepository.findNeighborhoodById(electionRoundInDTO.getNeighborhoodId());
        if (neighborhood == null) {
            throw new ApiException("Neighborhood not found");
        }

        ElectionRound electionRound = new ElectionRound();

        electionRound.setStartDate(electionRoundInDTO.getStartDate());
        electionRound.setEndDate(electionRoundInDTO.getEndDate());
        electionRound.setStatus("ACTIVE");
        electionRound.setNeighborhood(neighborhood);

        electionRoundRepository.save(electionRound);
    }


    // =========================
    // UPDATE ELECTION ROUND
    // =========================

    public void updateElectionRound(Integer roundId,
                                    ElectionRoundInDTO electionRoundInDTO) {

        ElectionRound oldElectionRound = getRoundOrThrow(roundId);
        if (!electionRoundInDTO.getEndDate().isAfter(electionRoundInDTO.getStartDate())) {
            throw new ApiException("End date must be after start date");
        }

        Neighborhood neighborhood = neighborhoodRepository.findNeighborhoodById(electionRoundInDTO.getNeighborhoodId());
        if (neighborhood == null) {
            throw new ApiException("Neighborhood not found");
        }

        oldElectionRound.setStartDate(electionRoundInDTO.getStartDate());
        oldElectionRound.setEndDate(electionRoundInDTO.getEndDate());
        oldElectionRound.setNeighborhood(neighborhood);

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
        LocalDate today = LocalDate.now();
        LocalDate termEndDate = today.plusYears(1);

        if (winningUser.getNeighborhood() == null) {
            throw new ApiException("Winning user neighborhood is required");
        }

        MayorProfile currentActiveMayor =
                mayorProfileRepository.findTopByNeighborhoodIdAndStatusOrderByStartDateDesc(
                        winningUser.getNeighborhood().getId(),
                        "ACTIVE"
                );

        if (currentActiveMayor != null
                && currentActiveMayor.getUser() != null
                && !currentActiveMayor.getUser().getId().equals(winningUser.getId())) {
            deactivateMayorProfile(currentActiveMayor, today);
        }

        winningUser.setStatus("MAYOR");
        winningUser.setMayorActive(true);
        winningUser.setMayorStartDate(today);
        winningUser.setMayorEndDate(termEndDate);
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
        mayorProfile.setStartDate(today);
        mayorProfile.setEndDate(termEndDate);
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
        trySendMayorAppointmentWhatsApp(winningUser, savedMayorProfile);
    }

    private void trySendMayorAppointmentWhatsApp(User mayor, MayorProfile mayorProfile) {
        try {
            if (mayor.getPhone() == null || mayor.getPhone().isBlank()) {
                return;
            }

            String neighborhoodName =
                    mayorProfile.getNeighborhood() == null
                            ? "your neighborhood"
                            : mayorProfile.getNeighborhood().getName();

            String message =
                    "Congratulations " + mayor.getFullName() + "\n\n" +
                            "You have been appointed as mayor of " + neighborhoodName + ".\n" +
                            "Term start: " + mayorProfile.getStartDate() + "\n" +
                            "Term end: " + mayorProfile.getEndDate();

            whatsAppService.sendWhatsAppMessage(mayor.getPhone(), message);
        } catch (Exception e) {
            log.error("Mayor appointment WhatsApp failed for user id {}.", mayor.getId(), e);
        }
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

        if (currentMayorProfile.getEndDate() == null || !today.isAfter(currentMayorProfile.getEndDate())) {
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

        deactivateMayorProfile(currentMayorProfile, today);

        ElectionRound nextRound = new ElectionRound();

        nextRound.setStartDate(today);
        nextRound.setEndDate(today.plusDays(1));
        nextRound.setStatus("ACTIVE");
        nextRound.setNeighborhood(currentMayorProfile.getNeighborhood());

        electionRoundRepository.save(nextRound);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void processExpiredMayorTerms() {
        LocalDate today = LocalDate.now();

        for (MayorProfile mayorProfile : mayorProfileRepository.findByStatus("ACTIVE")) {
            if (mayorProfile.getEndDate() == null || !today.isAfter(mayorProfile.getEndDate())) {
                continue;
            }

            if (mayorProfile.getNeighborhood() == null) {
                continue;
            }

            deactivateMayorProfile(mayorProfile, today);

            boolean hasActiveRoundNow =
                    electionRoundRepository.existsByStatusAndNeighborhoodId(
                            "ACTIVE",
                            mayorProfile.getNeighborhood().getId()
                    );

            if (hasActiveRoundNow) {
                continue;
            }

            ElectionRound nextRound = new ElectionRound();
            nextRound.setStartDate(today);
            nextRound.setEndDate(today.plusDays(1));
            nextRound.setStatus("ACTIVE");
            nextRound.setNeighborhood(mayorProfile.getNeighborhood());

            electionRoundRepository.save(nextRound);
        }
    }

    private void deactivateMayorProfile(MayorProfile mayorProfile, LocalDate endDate) {
        User oldMayor = mayorProfile.getUser();

        if (oldMayor != null) {
            oldMayor.setStatus("RESIDENT");
            oldMayor.setMayorActive(false);
            oldMayor.setMayorEndDate(endDate);
            userRepository.save(oldMayor);
        }

        mayorProfile.setStatus("INACTIVE");
        mayorProfile.setEndDate(endDate);
        mayorProfileRepository.save(mayorProfile);
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
