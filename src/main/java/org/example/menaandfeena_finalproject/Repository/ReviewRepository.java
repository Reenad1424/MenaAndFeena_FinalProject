package org.example.menaandfeena_finalproject.Repository;


import org.example.menaandfeena_finalproject.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findReviewById(Integer id);
    List<Review> findByEvent_Id(Integer eventId);
    List<Review> findByInitiative_Id(Integer initiativeId);


}
