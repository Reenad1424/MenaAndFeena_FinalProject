package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Ticket findTicketByTicketCode(String ticketCode);
    Ticket findByEventRegistration_Id(Integer eventRegistrationId);
    boolean existsByTicketCode(String ticketCode);
}
