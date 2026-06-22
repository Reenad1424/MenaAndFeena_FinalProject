package org.example.menaandfeena_finalproject.DTO.In;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventInDTO {

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime date;

    // وقت نهاية الفعالية (اختياري). إن وُجد، يجب أن يكون بعد وقت البداية.
    private LocalDateTime endTime;

    @NotEmpty(message = "Location cannot be empty")
    private String location;

    @NotNull(message = "Paid status cannot be null")
    private Boolean isPaid;

    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @NotNull(message = "Max participants cannot be null")
    @Positive(message = "Max participants must be positive")
    private Integer maxParticipants;

    // معرّفات الميزات المختارة من القائمة المعرّفة مسبقاً (اختياري).
    private List<Integer> featureIds;

    // فقرات برنامج الفعالية التي يدخلها المنظّم يدوياً (اختياري).
    @Valid
    private List<EventScheduleInDTO> schedule;

}