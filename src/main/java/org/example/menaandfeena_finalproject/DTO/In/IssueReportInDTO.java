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

    @NotBlank(message = "Street name cannot be blank")
    private String reportedStreetName;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

}
