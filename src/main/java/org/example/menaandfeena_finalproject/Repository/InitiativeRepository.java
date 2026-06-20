package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface InitiativeRepository extends JpaRepository<Initiative, Integer> {

    Initiative findInitiativeById(Integer id);

    List<Initiative> findInitiativesByCategory(String category);

    List<Initiative> findInitiativesByDateAfter(LocalDate date);

    List<Initiative> findTop3ByNeighborhoodOrderByDateDesc(Neighborhood neighborhood);

    Integer countByNeighborhoodAndStatus(Neighborhood neighborhood, String status);

    Integer countByStatus(String status);
    List<Initiative> findByCreatorId(Integer userId);

    Integer countByNeighborhood_Id(Integer neighborhoodId);

    List<Initiative> findByNeighborhood_Id(Integer neighborhoodId);

    Integer countByNeighborhood(Neighborhood neighborhood);
}