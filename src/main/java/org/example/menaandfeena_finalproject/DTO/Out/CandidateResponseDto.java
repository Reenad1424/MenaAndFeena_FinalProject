package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponseDto {

    private Integer candidateId;

    private String fullName;

    private String gender;

    private Integer totalVotes;

    private String status;

    private Boolean isWinner;
}