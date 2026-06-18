package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueReportInDTO {

    @NotBlank(message = "Title cannot be null")
    @Size(min = 5, max = 100, message = "Length must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    private String detectedDistrictName; // should be taken from the lng and lat

    private String detectedStreetName; // should be taken from the lng and lat

    private String imageUrl; // this will be changed to be a model

    @NotNull(message = "Reporter id cannot be null")
    private Integer reporterId;

    @NotNull(message = "Report neighborhood id cannot be null")
    private Integer reportNeighborhoodId; // should be taken from the lng and lat
}
