package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NeighborhoodDashboardDTO {

    private Integer neighborhoodId;
    private String neighborhoodName;
    private String city;

    // Estimated by AI
    private Integer estimatedPopulation;
    private Integer estimatedMales;
    private Integer estimatedFemales;

    // Real registered data from DB
    private Integer registeredPopulation;
    private Integer registeredMales;
    private Integer registeredFemales;

    private Integer childrenCount;
    private Integer adultsCount;
    private Integer elderlyCount;

    private Integer eventsCount;
    private Integer initiativesCount;
    private Integer openIssueReportsCount;
    private Integer marketplaceItemsCount;

    private List<EventDTO> lastEvents;
    private List<InitiativeDTO> lastInitiatives;
    private List<IssueReportDTO> lastIssues;
}