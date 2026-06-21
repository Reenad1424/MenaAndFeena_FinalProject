package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MayorProfileRepository extends JpaRepository<MayorProfile, Integer> {
    MayorProfile findMayorProfileById(Integer id);

    MayorProfile findTopByUserIdOrderByStartDateDesc(Integer userId);


    MayorProfile findTopByNeighborhoodIdAndStatusOrderByStartDateDesc(
            Integer neighborhoodId,
            String status
    );

    boolean existsByUserIdAndStatus(
            Integer userId,
            String status
    );

    MayorProfile findTopByUserIdAndStatusOrderByStartDateDesc(
            Integer userId,
            String status
    );

    MayorProfile findMayorProfileByUserId(Integer userId);

    List<MayorProfile> findByStatus(String status);
}
