package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class NeighborhoodInDTO {

    @NotBlank(message = "Neighborhood name cannot be blank")
    private String name;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @PositiveOrZero(message = "Estimated population must be zero or positive")
    private Integer estimatedPopulation;

    private Double latitude;

    private Double longitude;
}
