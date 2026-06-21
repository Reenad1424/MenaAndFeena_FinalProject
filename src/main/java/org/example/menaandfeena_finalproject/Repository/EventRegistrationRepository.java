package org.example.menaandfeena_finalproject.Repository;


import com.fasterxml.jackson.annotation.JacksonAnnotation;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.example.menaandfeena_finalproject.Model.InitiativeParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration , Integer> {
    EventRegistration findEventRegistrationById(Integer id);
    boolean existsByUserIdAndEventId(Integer userId, Integer eventId);
    boolean existsByUserIdAndEventIdAndStatus(Integer userId, Integer eventId, String status);
    boolean existsByFamilyMemberIdAndEventId(Integer familyMemberId, Integer eventId);
    int countByEventId(Integer eventId);

    int countByUserIdAndStatus(Integer id, String confirmed);

    // داخل كلاس EventRegistrationRepository
    List<EventRegistration> findByUserId(Integer userId); // 👈 تأكدي أن نوع الإرجاع List وليس void
}
