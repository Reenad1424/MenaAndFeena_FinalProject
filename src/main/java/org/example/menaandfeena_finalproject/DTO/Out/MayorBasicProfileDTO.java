package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MayorBasicProfileDTO {

    private Integer mayorId;
    private String fullName;

    private String nationalId;

    private String neighborhoodName;

    private String status; // ACTIVE / INACTIVE

    private LocalDate startDate;

    private LocalDate endDate;
    private String reportsText;
}