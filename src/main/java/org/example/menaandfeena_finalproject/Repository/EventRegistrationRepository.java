package org.example.menaandfeena_finalproject.Repository;


import com.fasterxml.jackson.annotation.JacksonAnnotation;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration , Integer> {
    EventRegistration findEventRegistrationById(Integer id);
}
