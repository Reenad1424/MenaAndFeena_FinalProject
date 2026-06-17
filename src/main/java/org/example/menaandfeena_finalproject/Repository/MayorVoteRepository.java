package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MayorVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MayorVoteRepository extends JpaRepository<MayorVote, Integer> {
    MayorVote findMayorVoteById(Integer id);
}
