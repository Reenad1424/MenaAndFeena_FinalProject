package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.EventFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventFeatureRepository extends JpaRepository<EventFeature, Integer> {

    boolean existsByName(String name);

    EventFeature findEventFeatureById(Integer id);

    List<EventFeature> findByIdIn(List<Integer> ids);
}
