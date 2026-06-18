package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InquiryMessageOutDTO {
    private Integer id;
    private String content;
    private LocalDateTime sentAt;
    private Integer senderId;
    private Integer inquiryId;
}
