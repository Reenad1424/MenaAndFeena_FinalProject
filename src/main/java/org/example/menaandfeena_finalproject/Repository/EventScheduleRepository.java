package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.EventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EventScheduleRepository extends JpaRepository<EventSchedule, Integer> {

    // فقرات البرنامج مرتبة حسب sortOrder ثم الوقت.
    List<EventSchedule> findByEvent_IdOrderBySortOrderAscTimeAsc(Integer eventId);

    @Transactional
    void deleteByEvent_Id(Integer eventId);
}
