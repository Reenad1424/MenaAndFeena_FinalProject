package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.IssueReportInDTO;
import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Service.IssueReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/issue-reports")
@RequiredArgsConstructor
public class IssueReportController {

    private final IssueReportService issueReportService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(issueReportService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createIssueReport(@RequestBody @Valid IssueReportInDTO issueReportInDTO) {
        return ResponseEntity.status(200).body(issueReportService.createIssueReport(issueReportInDTO));
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
