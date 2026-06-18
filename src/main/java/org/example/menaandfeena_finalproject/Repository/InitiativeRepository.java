package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InitiativeRepository extends JpaRepository<Initiative, Integer> {
    Initiative findInitiativeById(Integer id);
    List<Initiative> findInitiativesByCategory(String category);
    List<Initiative> findInitiativesByDateAfter(LocalDate date);
}
