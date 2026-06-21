package org.example.menaandfeena_finalproject.DTO.Out;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MayorAnalyticsDTO {

    private MayorBasicProfileDTO basicInfo;

    private Integer totalReports; // = 3 ثابتة

    private List<MayorReportCardDTO> reports;
}