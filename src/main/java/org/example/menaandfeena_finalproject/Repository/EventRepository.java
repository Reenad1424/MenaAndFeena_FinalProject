package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findEventById(Integer id);
    List<Event> findEventsByDateAfter(LocalDateTime date);
    List<Event> findEventsByDateBefore(LocalDateTime date);
    List<Event> findEventsByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

}