package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueReportInDTO {

    @NotBlank(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    private String detectedDistrictName; // should be taken from the lng and lat

    private String detectedStreetName; // should be taken from the lng and lat

    private String imageUrl; // this will be changed to be a model

}
