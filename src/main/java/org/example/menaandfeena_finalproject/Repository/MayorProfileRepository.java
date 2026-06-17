package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MayorProfileRepository extends JpaRepository<MayorProfile, Integer> {
    MayorProfile findMayorProfileById(Integer id);
}
