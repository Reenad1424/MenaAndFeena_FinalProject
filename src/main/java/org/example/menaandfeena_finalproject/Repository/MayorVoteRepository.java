package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MayorVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MayorVoteRepository extends JpaRepository<MayorVote, Integer> {
    MayorVote findMayorVoteById(Integer id);
        int countByMayorCandidateId(Integer candidateId);
        boolean existsByUserIdAndElectionRoundId(Integer userId, Integer roundId);

    MayorVote findByUserIdAndElectionRoundId(Integer userId, Integer roundId);

    List<MayorVote> findByUserId(Integer userId);
}


