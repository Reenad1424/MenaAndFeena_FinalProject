package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekendPlanOutDTO {
    private List<WeekendPlanItemDTO> friday;
    private List<WeekendPlanItemDTO> saturday;
    private List<String> notes;
}
