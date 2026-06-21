package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVoteDTO {

    private Integer voteId;

    private String candidateName;

    private Integer electionRoundId;

    private Integer electionYear;

    private String electionStatus;

    private LocalDateTime votedAt;

}