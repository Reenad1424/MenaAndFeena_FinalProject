package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandmarkRepository extends JpaRepository<Landmark, Integer>
{ Landmark findLandmarkById(Integer id); }
