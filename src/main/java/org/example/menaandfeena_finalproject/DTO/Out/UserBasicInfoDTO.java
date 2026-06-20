package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicInfoDTO {

    private String fullName;
    private String neighborhoodName;
    private LocalDate memberSince;
    private Integer yearsInNeighborhood;
}