package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<?> getTicket(@PathVariable Integer registrationId,
                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getTicket(registrationId, user.getId()));
    }

    @PutMapping("/check-in/{ticketCode}")
    public ResponseEntity<?> checkInByTicketCode(@PathVariable String ticketCode,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.checkInByTicketCode(ticketCode, user.getId()));
    }
}
