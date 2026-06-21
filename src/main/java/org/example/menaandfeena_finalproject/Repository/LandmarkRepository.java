package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Landmark;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;@Repository
public interface LandmarkRepository extends JpaRepository<Landmark, Integer> {

    Landmark findLandmarkById(Integer id);

    List<Landmark> findByNeighborhood(
            Neighborhood neighborhood
    );

    List<Landmark> findByNeighborhoodAndType(
            Neighborhood neighborhood,
            String type
    );

    boolean existsByNameAndTypeAndNeighborhoodId(
            String name,
            String type,
            Integer neighborhoodId
    );
}