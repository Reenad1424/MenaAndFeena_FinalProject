package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.ElectionRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRoundRepository extends JpaRepository<ElectionRound, Integer> {
    ElectionRound findElectionRoundById(Integer id);

    boolean existsByStatus(String active);

    List<ElectionRound> findByNeighborhoodId(Integer id);

    boolean existsByStatusAndNeighborhoodId(String active, Integer id);
}
