package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.IssueReportInDTO;
import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Service.IssueReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/issue-reports")
@RequiredArgsConstructor
public class IssueReportController {

    private final IssueReportService issueReportService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(issueReportService.getAll());
    }

    @PostMapping("/user/{reporterId}/add")
    public ResponseEntity<?> createIssueReport(@PathVariable Integer reporterId, @RequestBody @Valid IssueReportInDTO issueReportInDTO) {
        return ResponseEntity.status(200).body(issueReportService.createIssueReport(reporterId, issueReportInDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIssueReportById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getIssueReportsByStatus(@PathVariable String status) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByStatus(status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<?> getIssueReportsByPriority(@PathVariable String priority) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByPriority(priority));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getIssueReportsByCategory(@PathVariable String category) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByCategory(category));
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<?> getMyIssueReports(@PathVariable Integer userId) {
        // TODO: later this will use the authenticated user from Spring Security/JWT.
        return ResponseEntity.status(200).body(issueReportService.getMyIssueReports(userId));
    }

    @GetMapping("/user/{userId}/neighborhood")
    public ResponseEntity<?> getUserNeighborhoodReports(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(issueReportService.getUserNeighborhoodReports(userId));
    }

    @GetMapping("/user/{userId}/my-reports")
    public ResponseEntity<?> getUserMyReports(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(issueReportService.getUserMyReports(userId));
    }

    @GetMapping("/user/{userId}/report/{reportId}")
    public ResponseEntity<?> getUserIssueReportById(@PathVariable Integer userId, @PathVariable Integer reportId) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportById(userId, reportId));
    }

    @PostMapping(value = "/user/{userId}/report/{reportId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadIssueReportImage(@PathVariable Integer userId, @PathVariable Integer reportId, @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(200).body(issueReportService.uploadIssueReportImage(userId, reportId, image));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getUserIssueReportsByStatus(@PathVariable Integer userId, @PathVariable String status) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportsByStatus(userId, status));
    }

    @GetMapping("/user/{userId}/priority/{priority}")
    public ResponseEntity<?> getUserIssueReportsByPriority(@PathVariable Integer userId, @PathVariable String priority) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportsByPriority(userId, priority));
    }

    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<?> getUserIssueReportsByCategory(@PathVariable Integer userId, @PathVariable String category) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportsByCategory(userId, category));
    }

    @GetMapping("/user/{userId}/search")
    public ResponseEntity<?> searchUserIssueReports(@PathVariable Integer userId, @RequestParam String keyword) {
        return ResponseEntity.status(200).body(issueReportService.searchUserIssueReports(userId, keyword));
    }

    @GetMapping("/mayor-report/{userId}/pdf")
    public ResponseEntity<byte[]> generateMayorIssueReportPdf(@PathVariable Integer userId) {
        byte[] pdfBytes = issueReportService.generateMayorIssueReportPdf(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Neighborhood-Issue-Report.pdf\"");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/neighborhood/{neighborhoodId}")
    public ResponseEntity<?> getIssueReportsByNeighborhood(@PathVariable Integer neighborhoodId) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByNeighborhood(neighborhoodId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchIssueReports(@RequestParam String keyword) {
        return ResponseEntity.status(200).body(issueReportService.searchIssueReports(keyword));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateIssueReportStatus(@PathVariable Integer id, @RequestParam String status) {
        issueReportService.updateIssueReportStatus(id, status);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report status updated successfully"));
    }

    @PutMapping("/{id}/start-progress")
    public ResponseEntity<?> startProgress(@PathVariable Integer id) {
        issueReportService.startProgress(id);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report moved to in progress successfully"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeReport(@PathVariable Integer id) {
        issueReportService.completeReport(id);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report completed successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid IssueReport issueReport) {
        issueReportService.update(id, issueReport);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        issueReportService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report deleted successfully"));
    }
}
