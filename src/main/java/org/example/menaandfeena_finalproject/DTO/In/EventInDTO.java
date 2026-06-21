package org.example.menaandfeena_finalproject.DTO.In;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @NotEmpty(message = "Location cannot be empty")
    private String location;

    @NotNull(message = "Paid status cannot be null")
    private Boolean isPaid;

    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @NotNull(message = "Max participants cannot be null")
    @Positive(message = "Max participants must be positive")
    private Integer maxParticipants;

    @Pattern(regexp = "ACTIVE|CANCELLED|COMPLETED", message = "Status must be ACTIVE, CANCELLED, or COMPLETED")
    private String status;
}