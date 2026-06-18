package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InquiryOutDTO {
    private Integer id;
    private String subject;
    private String status;
    private LocalDateTime createdAt;
    private Integer requesterId;
    private Integer targetUserId;
    private Integer marketPlaceItemId;
    private Integer announcementId;
}
