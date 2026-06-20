package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    Event findEventById(Integer id);

    List<Event> findEventsByDateAfter(LocalDateTime date);

    List<Event> findEventsByDateBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findTop3ByNeighborhoodOrderByDateDesc(Neighborhood neighborhood);

    Integer countByNeighborhood(Neighborhood neighborhood);

    List<Event> findEventsByDateBefore(LocalDateTime now);

    List<Event> findByCreatorId(Integer userId);

    Integer countByNeighborhoodId(Integer neighborhoodId);
    Integer countByNeighborhood_Id(Integer neighborhoodId);


    List<Event> findByNeighborhood_Id(Integer neighborhoodId);
}