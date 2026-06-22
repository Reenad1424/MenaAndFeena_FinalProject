package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTicketOutDTO {
    private Integer registrationId;
    private String ticketCode;
    private String registrationStatus;
    private LocalDate registeredAt;
    private Boolean checkedIn;
    private LocalDateTime checkedInAt;
    private Integer eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventLocation;
    private Integer userId;
    private String userFullName;
    private Integer familyMemberId;
    private String familyMemberName;
}
