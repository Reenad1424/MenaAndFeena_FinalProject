package org.example.menaandfeena_finalproject.DTO.Out;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MayorReportsDTO {

    private List<IssueReportDTO> urgentReports;
    private List<IssueReportDTO> nonUrgentReports;
    private List<IssueReportDTO> periodicReports;
}
