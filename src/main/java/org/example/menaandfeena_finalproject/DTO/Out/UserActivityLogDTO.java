package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityLogDTO {
    private Integer userId;
    private String fullName;
    private List<UserIssueDTO> issueReports;
    private List<UserEventDTO> registeredEvents;
    private List<UserInitiativeDTO> participatedInitiatives;
}