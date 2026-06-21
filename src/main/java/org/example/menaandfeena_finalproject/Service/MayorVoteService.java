package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MayorVoteInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MayorVoteOutDTO;
import org.example.menaandfeena_finalproject.Model.ElectionRound;
import org.example.menaandfeena_finalproject.Model.MayorCandidate;
import org.example.menaandfeena_finalproject.Model.MayorVote;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.ElectionRoundRepository;
import org.example.menaandfeena_finalproject.Repository.MayorCandidateRepository;
import org.example.menaandfeena_finalproject.Repository.MayorVoteRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class MayorVoteService {

    private final MayorVoteRepository mayorVoteRepository;
    private final MayorCandidateRepository mayorCandidateRepository;
    private final ElectionRoundRepository electionRoundRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    //Reenad
    // =========================
    // GET ALL MAYOR VOTES
    // =========================

    public List<MayorVoteOutDTO> getAllMayorVotes() {

        List<MayorVoteOutDTO> mayorVoteOutDTOS = new ArrayList<>();

        for (MayorVote mayorVote : mayorVoteRepository.findAll()) {
            mayorVoteOutDTOS.add(toOutDTO(mayorVote));
        }

        return mayorVoteOutDTOS;
    }


    // =========================
    // VOTE FOR MAYOR CANDIDATE
    // =========================

    public String voteForMayorCandidate(Integer userId,
                                        Integer candidateId,
                                        Integer roundId) {

        User voter = getVoterOrThrow(userId);

        MayorCandidate candidate = getCandidateOrThrow(candidateId);

        ElectionRound round = getRoundOrThrow(roundId);

        validateVotingRules(voter, candidate, round);

        MayorVote existingVote =
                mayorVoteRepository.findByUserIdAndElectionRoundId(
                        userId,
                        roundId
                );

        if (existingVote != null) {
            return handleExistingVote(existingVote, candidate);
        }

        MayorVote vote = new MayorVote();

        vote.setUser(voter);
        vote.setMayorCandidate(candidate);
        vote.setElectionRound(round);
        vote.setCreatedAt(LocalDateTime.now());

        mayorVoteRepository.save(vote);

        return "تم تسجيل صوتك بنجاح للمرشح "
                + candidate.getUser().getFullName();
    }


    // =========================
    // UPDATE MAYOR VOTE
    // =========================

    public void updateMayorVote(Integer voteId,
                                MayorVoteInDTO mayorVoteInDTO) {

        MayorVote oldMayorVote =
                mayorVoteRepository.findMayorVoteById(voteId);

        if (oldMayorVote == null) {
            throw new ApiException("Mayor vote not found");
        }

        oldMayorVote.setCreatedAt(mayorVoteInDTO.getCreatedAt());

        mayorVoteRepository.save(oldMayorVote);
    }


    // =========================
    // DELETE MAYOR VOTE
    // =========================

    public void deleteMayorVote(Integer voteId) {

        MayorVote oldMayorVote =
                mayorVoteRepository.findMayorVoteById(voteId);

        if (oldMayorVote == null) {
            throw new ApiException("Mayor vote not found");
        }

        mayorVoteRepository.delete(oldMayorVote);
    }


    // =========================
    // PRIVATE HELPERS
    // =========================

    private User getVoterOrThrow(Integer userId) {

        User voter = userRepository.findUserById(userId);

        if (voter == null) {
            throw new ApiException("المستخدم غير موجود");
        }

        if (voter.getNeighborhood() == null) {
            throw new ApiException("المستخدم غير مرتبط بأي حي");
        }

        return voter;
    }


    private MayorCandidate getCandidateOrThrow(Integer candidateId) {

        MayorCandidate candidate =
                mayorCandidateRepository.findById(candidateId).orElse(null);

        if (candidate == null) {
            throw new ApiException("المرشح غير موجود");
        }

        if (candidate.getUser() == null ||
                candidate.getUser().getNeighborhood() == null) {

            throw new ApiException("المرشح غير مرتبط بحي");
        }

        return candidate;
    }


    private ElectionRound getRoundOrThrow(Integer roundId) {

        ElectionRound round =
                electionRoundRepository.findById(roundId).orElse(null);

        if (round == null) {
            throw new ApiException("الجولة الانتخابية غير موجودة");
        }

        if (round.getNeighborhood() == null) {
            throw new ApiException("الجولة الانتخابية غير مرتبطة بحي");
        }

        return round;
    }


    private void validateVotingRules(User voter,
                                     MayorCandidate candidate,
                                     ElectionRound round) {

        validateRoundIsActive(round);

        validateCandidateBelongsToRound(candidate, round);

        validateVoterNeighborhood(voter, round);

        validateCandidateNeighborhood(candidate, round);

        validateNotVotingForSelf(voter, candidate);

        validateCandidateStatus(candidate);

        validateVoterAge(voter);
    }


    private void validateRoundIsActive(ElectionRound round) {

        if (!"ACTIVE".equalsIgnoreCase(round.getStatus())) {
            throw new ApiException("عذراً، التصويت مغلق في هذه الدورة الانتخابية حالياً");
        }
    }


    private void validateCandidateBelongsToRound(MayorCandidate candidate,
                                                 ElectionRound round) {

        if (candidate.getElectionRound() == null ||
                !candidate.getElectionRound().getId().equals(round.getId())) {

            throw new ApiException("هذا المرشح لا ينتمي لهذه الجولة الانتخابية");
        }
    }


    private void validateVoterNeighborhood(User voter,
                                           ElectionRound round) {

        if (!voter.getNeighborhood().getId()
                .equals(round.getNeighborhood().getId())) {

            throw new ApiException("لا يمكنك التصويت في جولة انتخابية خارج حيّك");
        }
    }


    private void validateCandidateNeighborhood(MayorCandidate candidate,
                                               ElectionRound round) {

        if (!candidate.getUser().getNeighborhood().getId()
                .equals(round.getNeighborhood().getId())) {

            throw new ApiException("لا يمكنك التصويت لمرشح خارج حي هذه الجولة");
        }
    }


    private void validateNotVotingForSelf(User voter,
                                          MayorCandidate candidate) {

        if (voter.getId().equals(candidate.getUser().getId())) {
            throw new ApiException("لا يمكنك التصويت لنفسك");
        }
    }


    private void validateCandidateStatus(MayorCandidate candidate) {

        if (!"CANDIDATE".equalsIgnoreCase(candidate.getStatus())) {
            throw new ApiException("لا يمكنك التصويت إلا لمرشح في هذه الجولة");
        }
    }


    private void validateVoterAge(User voter) {

        Integer voterAge =
                userService.calculateAge(voter.getBirthDate());

        if (voterAge == null || voterAge < 18) {
            throw new ApiException("عذراً، يجب أن يكون عمرك 18 سنة أو أكثر لتتمكن من التصويت!");
        }
    }


    private String handleExistingVote(MayorVote existingVote,
                                      MayorCandidate newCandidate) {

        if (existingVote.getMayorCandidate().getId()
                .equals(newCandidate.getId())) {

            mayorVoteRepository.delete(existingVote);

            return "تم سحب صوتك بنجاح من المرشح "
                    + newCandidate.getUser().getFullName();
        }

        existingVote.setMayorCandidate(newCandidate);

        mayorVoteRepository.save(existingVote);

        return "تم تغيير صوتك بنجاح إلى المرشح "
                + newCandidate.getUser().getFullName();
    }


    private MayorVoteOutDTO toOutDTO(MayorVote mayorVote) {

        return new MayorVoteOutDTO(
                mayorVote.getId(),
                mayorVote.getCreatedAt()
        );
    }
}