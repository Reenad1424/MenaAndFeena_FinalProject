package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LandmarkInDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Type cannot be blank")
    @Pattern(regexp = "MOSQUE|SCHOOL|PARK|HOSPITAL|OTHER", message = "Type must be MOSQUE, SCHOOL, PARK, HOSPITAL, or OTHER")
    private String type;

    private Double latitude;
    private Double longitude;

    @NotNull(message = "Neighborhood id cannot be null")
    private Integer neighborhoodId;
}
