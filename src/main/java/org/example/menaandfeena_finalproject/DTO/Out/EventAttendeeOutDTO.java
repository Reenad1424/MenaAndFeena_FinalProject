package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAttendeeOutDTO {
    private Integer registrationId;
    private String status;
    private LocalDate registeredAt;
    private String ticketCode;
    private Boolean checkedIn;
    private LocalDateTime checkedInAt;
    private Integer userId;
    private String userFullName;
    private Integer familyMemberId;
    private String familyMemberName;
}
