package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.AIModeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIModerationRepository extends JpaRepository<AIModeration, Integer>
{ AIModeration findAIModerationById(Integer id); }
