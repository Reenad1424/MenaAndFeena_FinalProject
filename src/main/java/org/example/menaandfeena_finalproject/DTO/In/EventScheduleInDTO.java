package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventScheduleInDTO {

    // الوقت بصيغة HH:mm (24 ساعة) مثل 09:30 أو 18:00.
    @NotBlank(message = "Schedule time cannot be blank")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Schedule time must be valid HH:mm")
    private String time;

    @NotBlank(message = "Schedule title cannot be blank")
    private String title;

    private Integer sortOrder;
}
