package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileReputationDTO {

    private UserBasicInfoDTO basicInfo;

    private List<UserReviewDTO> writtenReviews;
    private List<UserReviewDTO> receivedReviews;

    private List<UserIssueDTO> issueReports;
}
