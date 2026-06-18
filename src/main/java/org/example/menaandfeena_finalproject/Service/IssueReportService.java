package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.IssueReportInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.IssueReportOutDTO;
import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.IssueReportRepository;
import org.example.menaandfeena_finalproject.Repository.NeighborhoodRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueReportService {
    private static final DateTimeFormatter ISSUE_REPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private final IssueReportRepository issueReportRepository;
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final OpenAIService openAIService;

    public List<IssueReportOutDTO> getAll() {
        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();

        for (IssueReport issueReport : issueReportRepository.findAll()) {
            issueReportOutDTOS.add(toOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }


    public IssueReportOutDTO createIssueReport(IssueReportInDTO issueReportInDTO) {
        User reporter = userRepository.findUserById(issueReportInDTO.getReporterId());

        if (reporter == null) {
            throw new ApiException("Reporter not found");
        }

        Neighborhood reportNeighborhood = neighborhoodRepository.findNeighborhoodById(issueReportInDTO.getReportNeighborhoodId());

        if (reportNeighborhood == null) {
            throw new ApiException("Report neighborhood not found");
        }

        IssueReport issueReport = new IssueReport();
        issueReport.setTitle(issueReportInDTO.getTitle());
        issueReport.setDescription(issueReportInDTO.getDescription());
        issueReport.setLatitude(issueReportInDTO.getLatitude());
        issueReport.setLongitude(issueReportInDTO.getLongitude());
        issueReport.setDetectedDistrictName(issueReportInDTO.getDetectedDistrictName());
        issueReport.setDetectedStreetName(issueReportInDTO.getDetectedStreetName());
        issueReport.setImageUrl(issueReportInDTO.getImageUrl());

        // This prompt forces the AI to choose only from our database-supported values.
        String systemPrompt = """
                You classify municipal/community issue reports.
                Choose exactly one category from:
                LIGHTING, ROADS, CLEANLINESS, VISUAL_POLLUTION, PARKS, WATER_AND_SEWAGE, ANIMALS, SAFETY, OTHER.
                Choose exactly one priority from:
                URGENT, NON_URGENT, PERIODIC.
                Return only this format with no explanation:
                CATEGORY|PRIORITY
                """;

        // The AI only needs the user's report text to classify category and priority.
        String userContent = "Title: " + issueReportInDTO.getTitle() + "\nDescription: " + issueReportInDTO.getDescription();
        String aiResult = openAIService.askAI(systemPrompt, userContent);

        // Safe fallback values in case OpenAI is down or returns an unexpected format.
        String category = "OTHER";
        String priority = "NON_URGENT";

        if (aiResult != null) {
            // Expected AI response format: CATEGORY|PRIORITY, for example ROADS|URGENT.
            String[] aiParts = aiResult.trim().toUpperCase().replace(" ", "").split("\\|");
            if (aiParts.length == 2
                    && aiParts[0].matches("LIGHTING|ROADS|CLEANLINESS|VISUAL_POLLUTION|PARKS|WATER_AND_SEWAGE|ANIMALS|SAFETY|OTHER")
                    && aiParts[1].matches("URGENT|NON_URGENT|PERIODIC")) {
                category = aiParts[0];
                priority = aiParts[1];
            }
        }

        issueReport.setPriority(priority);
        issueReport.setCategory(category);
        issueReport.setStatus("OPEN");
        issueReport.setReporter(reporter);
        issueReport.setReportNeighborhood(reportNeighborhood);

        return toOutDTO(issueReportRepository.save(issueReport));
    }



    public void update(Integer id, IssueReport issueReport) {
        IssueReport old = issueReportRepository.findIssueReportById(id);
        if (old == null) throw new ApiException("Issue report not found");
        old.setTitle(issueReport.getTitle());
        old.setDescription(issueReport.getDescription());
        old.setCategory(issueReport.getCategory());
        old.setPriority(issueReport.getPriority());
        old.setStatus(issueReport.getStatus());
        old.setLatitude(issueReport.getLatitude());
        old.setLongitude(issueReport.getLongitude());
        old.setDetectedDistrictName(issueReport.getDetectedDistrictName());
        old.setDetectedStreetName(issueReport.getDetectedStreetName());
        old.setImageUrl(issueReport.getImageUrl());
        old.setReportNeighborhood(issueReport.getReportNeighborhood());
        old.setReporter(issueReport.getReporter());
        issueReportRepository.save(old);
    }

    public void delete(Integer id) {
        IssueReport issueReport = issueReportRepository.findIssueReportById(id);
        if (issueReport == null) throw new ApiException("Issue report not found");
        issueReportRepository.delete(issueReport);
    }

    // Abdullah
    public IssueReportOutDTO getIssueReportById(Integer id) {
        IssueReport issueReport = issueReportRepository.findIssueReportById(id);

        if (issueReport == null) {
            throw new ApiException("Issue report not found");
        }

        return toOutDTO(issueReport);
    }

    public List<IssueReportOutDTO> getIssueReportsByStatus(String status) {
        if (!status.matches("OPEN|IN_PROGRESS|COMPLETED")) {
            throw new ApiException("Status must be OPEN, IN_PROGRESS or COMPLETED only");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByStatus(status)) {
            issueReportOutDTOS.add(toOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getIssueReportsByPriority(String priority) {
        if (!priority.matches("URGENT|NON_URGENT|PERIODIC")) {
            throw new ApiException("Priority must be URGENT, NON_URGENT or PERIODIC only");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByPriority(priority)) {
            issueReportOutDTOS.add(toOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getIssueReportsByCategory(String category) {
        if (!category.matches("LIGHTING|ROADS|CLEANLINESS|VISUAL_POLLUTION|PARKS|WATER_AND_SEWAGE|ANIMALS|SAFETY|OTHER")) {
            throw new ApiException("Category must be LIGHTING, ROADS, CLEANLINESS, VISUAL_POLLUTION, PARKS, WATER_AND_SEWAGE, ANIMALS, SAFETY or OTHER only");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByCategory(category)) {
            issueReportOutDTOS.add(toOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getMyIssueReports(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByReporterId(userId)) {
            issueReportOutDTOS.add(toOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getIssueReportsByNeighborhood(Integer neighborhoodId) {
        Neighborhood neighborhood = neighborhoodRepository.findNeighborhoodById(neighborhoodId);

        if (neighborhood == null) {
            throw new ApiException("Neighborhood not found");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByReportNeighborhoodId(neighborhoodId)) {
            issueReportOutDTOS.add(toOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> searchIssueReports(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ApiException("Keyword cannot be blank");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.searchIssueReports(keyword)) {
            issueReportOutDTOS.add(toOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public void updateIssueReportStatus(Integer reportId, String status) {
        if (!status.matches("OPEN|IN_PROGRESS|COMPLETED")) {
            throw new ApiException("Status must be OPEN, IN_PROGRESS or COMPLETED only");
        }

        IssueReport report = issueReportRepository.findIssueReportById(reportId);

        if (report == null) {
            throw new ApiException("Issue report not found");
        }

        if (report.getStatus().equals("COMPLETED")) {
            throw new ApiException("Completed reports cannot be changed");
        }

        if (status.equals("OPEN") && !report.getStatus().equals("OPEN")) {
            throw new ApiException("Issue report cannot be moved back to open");
        }

        if (status.equals("IN_PROGRESS") && !report.getStatus().equals("OPEN")) {
            throw new ApiException("Only open reports can be moved to in progress");
        }

        if (status.equals("COMPLETED") && !report.getStatus().equals("IN_PROGRESS")) {
            throw new ApiException("Only in progress reports can be completed");
        }

        report.setStatus(status);
        issueReportRepository.save(report);
    }

    public void startProgress(Integer reportId) {
        IssueReport report = issueReportRepository.findIssueReportById(reportId);

        if (report == null) {
            throw new ApiException("Issue report not found");
        }

        if (report.getStatus().equals("COMPLETED")) {
            throw new ApiException("Completed reports cannot be moved to in progress");
        }

        if (!report.getStatus().equals("OPEN")) {
            throw new ApiException("Only open reports can be moved to in progress");
        }

        report.setStatus("IN_PROGRESS");
        issueReportRepository.save(report);
    }

    public void completeReport(Integer reportId) {
        IssueReport report = issueReportRepository.findIssueReportById(reportId);

        if (report == null) {
            throw new ApiException("Issue report not found");
        }

        if (report.getStatus().equals("COMPLETED")) {
            throw new ApiException("Issue report is already completed");
        }

        if (!report.getStatus().equals("IN_PROGRESS")) {
            throw new ApiException("Only in progress reports can be completed");
        }

        report.setStatus("COMPLETED");
        issueReportRepository.save(report);
    }

    private IssueReportOutDTO toOutDTO(IssueReport issueReport) {
        Integer reporterId = issueReport.getReporter() == null ? null : issueReport.getReporter().getId();
        String reporterName = issueReport.getReporter() == null ? null : issueReport.getReporter().getFullName();
        Integer reportNeighborhoodId = issueReport.getReportNeighborhood() == null ? null : issueReport.getReportNeighborhood().getId();
        String reportNeighborhoodName = issueReport.getReportNeighborhood() == null ? null : issueReport.getReportNeighborhood().getName();
        String createdAt = issueReport.getCreatedAt() == null ? null : issueReport.getCreatedAt().format(ISSUE_REPORT_DATE_FORMAT);

        return new IssueReportOutDTO(
                issueReport.getId(),
                issueReport.getTitle(),
                issueReport.getDescription(),
                issueReport.getCategory(),
                issueReport.getPriority(),
                issueReport.getStatus(),
                issueReport.getLatitude(),
                issueReport.getLongitude(),
                createdAt,
                issueReport.getDetectedDistrictName(),
                issueReport.getDetectedStreetName(),
                issueReport.getImageUrl(),
                reporterId,
                reporterName,
                reportNeighborhoodId,
                reportNeighborhoodName
        );
    }
}
