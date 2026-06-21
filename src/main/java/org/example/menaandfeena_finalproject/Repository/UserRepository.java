package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer id);

    User findUserByEmail(String email);

    List<User> findUsersByNeighborhoodId(Integer neighborhoodId);

    List<User> findByNeighborhoodId(Integer id);
}