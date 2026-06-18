package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInDTO {
    @NotNull(message = "User id cannot be null")
    private Integer userId;

    private LocalDate startDate;

    private LocalDate endDate;
}
