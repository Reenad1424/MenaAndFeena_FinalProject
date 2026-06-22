package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registeredAt;
    private Boolean checkedIn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkedInAt;
    private Integer eventId;
    private String eventTitle;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private String eventLocation;
    private Integer userId;
    private String userFullName;
    private Integer familyMemberId;
    private String familyMemberName;
}
