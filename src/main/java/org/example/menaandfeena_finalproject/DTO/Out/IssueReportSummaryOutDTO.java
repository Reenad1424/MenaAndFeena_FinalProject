package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IssueReportSummaryOutDTO {
    private Integer id;
    private String title;
    private String category;
    private String priority;
    private String status;
    private String createdAt;
    private String imageUrl;
}
