package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileCommunityDTO {

    private UserBasicInfoDTO basicInfo;

    private List<FamilyMemberDTO> familyMembers;

    private LastActivityDTO lastActivity;

    private List<UserVoteDTO> votes;
}