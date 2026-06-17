package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MayorCandidateOutDTO {
    private Integer id;
    private LocalDateTime appliedAt;
    private String status;
}
