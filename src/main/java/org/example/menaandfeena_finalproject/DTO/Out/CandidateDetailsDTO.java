package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDetailsDTO {

    private Integer candidateId;

    private String fullName;

    private String neighborhoodName;

    private Integer memberSinceYear;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedAt;

    private Integer totalVotes;

    private Integer organizedEventsCount;

    private Integer joinedInitiativesCount;

    private Integer resolvedIssuesCount;

    private Integer neighborInteractions;

    private List<String> initiatives;

    private List<String> events;
}