package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IssueReportOutDTO {
    private Integer id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private Double latitude;
    private Double longitude;
    private String createdAt;
    private String reportedStreetName;
    private String detectedDistrictName;
    private String detectedStreetName;
    private String imageUrl;
    private Integer reporterId;
    private String reporterName;
    private Integer reportNeighborhoodId;
    private String reportNeighborhoodName;
}
