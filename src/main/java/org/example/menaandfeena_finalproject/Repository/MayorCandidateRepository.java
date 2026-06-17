package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MayorCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MayorCandidateRepository extends JpaRepository<MayorCandidate, Integer> {
    MayorCandidate findMayorCandidateById(Integer id);
}
