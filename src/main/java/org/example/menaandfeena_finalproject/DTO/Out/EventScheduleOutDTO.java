package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventScheduleOutDTO {
    private Integer id;
    private String time;
    private String title;
    private Integer sortOrder;
}
