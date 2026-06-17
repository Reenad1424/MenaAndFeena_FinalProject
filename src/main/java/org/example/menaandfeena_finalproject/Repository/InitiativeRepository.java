package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitiativeRepository extends JpaRepository<Initiative, Integer> {
    Initiative findInitiativeById(Integer id);
}
