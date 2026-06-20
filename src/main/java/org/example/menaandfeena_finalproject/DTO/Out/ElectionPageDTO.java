package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectionPageDTO {

    private Integer roundId;

    private String neighborhoodName;

    private String roundStatus;

    private String message;

    private Integer totalCandidates;

    private Integer totalVotes;

    private List<CandidateResponseDto> candidates;
}