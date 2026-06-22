package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekendPlanItemDTO {
    private String period;   // مثل: Morning / Evening
    private String activity;
    private String reason;
}
