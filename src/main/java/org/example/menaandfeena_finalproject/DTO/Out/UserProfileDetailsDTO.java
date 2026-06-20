package org.example.menaandfeena_finalproject.DTO.Out;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDetailsDTO {

        private UserBasicInfoDTO basicInfo;

        private List<FamilyMemberDTO> familyMembers;
        private LastActivityDTO lastActivity;

        private List<UserVoteDTO> votes;

        private List<UserEventDTO> participatedEvents;
        private List<UserEventDTO> createdEvents;

        private List<UserInitiativeDTO> participatedInitiatives;
        private List<UserInitiativeDTO> createdInitiatives;

        private List<UserReviewDTO> writtenReviews;
        private List<UserReviewDTO> receivedReviews;

        private List<UserIssueDTO> issueReports;

        private List<UserMarketItemDTO> marketplaceItems;

        private List<UserOrderDTO> purchases;
        private List<UserOrderDTO> sales;
    }