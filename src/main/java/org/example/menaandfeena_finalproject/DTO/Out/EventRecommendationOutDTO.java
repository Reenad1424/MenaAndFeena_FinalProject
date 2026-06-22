package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRecommendationOutDTO {
    private String familyMemberName;
    private String ageGroup;
    private String recommendedEvent;
    private String reason;
}
