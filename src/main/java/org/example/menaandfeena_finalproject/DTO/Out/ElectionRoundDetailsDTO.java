package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectionRoundDetailsDTO {

    private Integer roundId;
    private String neighborhoodName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String status;
    private String statusDescription;
    private Long daysRemaining;

    private Integer totalCandidates;
    private Integer totalVotes;

    private String message;

    private String winnerName;
    private Integer winnerVotes;
}