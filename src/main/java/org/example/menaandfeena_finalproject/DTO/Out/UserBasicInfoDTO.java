package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate memberSince;
    private Integer yearsInNeighborhood;
}