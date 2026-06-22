package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.IssueReportInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.IssueReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/issue-reports")
@RequiredArgsConstructor
public class IssueReportController {

    private final IssueReportService issueReportService;

    // ADMIN/DEBUG
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(issueReportService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createIssueReport(@AuthenticationPrincipal User user,
                                               @RequestBody @Valid IssueReportInDTO issueReportInDTO) {
        return ResponseEntity.status(200).body(issueReportService.createIssueReport(user.getId(), issueReportInDTO));
    }

    // ADMIN/DEBUG
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getIssueReportById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportById(id));
    }

    // ADMIN/DEBUG
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getIssueReportsByStatus(@PathVariable String status) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByStatus(status));
    }

    // ADMIN/DEBUG
    @GetMapping("/priority/{priority}")
    public ResponseEntity<?> getIssueReportsByPriority(@PathVariable String priority) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByPriority(priority));
    }

    // ADMIN/DEBUG
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getIssueReportsByCategory(@PathVariable String category) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByCategory(category));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyIssueReports(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(issueReportService.getMyIssueReports(user.getId()));
    }

    @GetMapping("/neighborhood")
    public ResponseEntity<?> getUserNeighborhoodReports(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(issueReportService.getUserNeighborhoodReports(user.getId()));
    }

    @GetMapping("/my-reports")
    public ResponseEntity<?> getUserMyReports(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(issueReportService.getUserMyReports(user.getId()));
    }

    @GetMapping("/report/{reportId}")
    public ResponseEntity<?> getUserIssueReportById(@AuthenticationPrincipal User user,
                                                    @PathVariable Integer reportId) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportById(user.getId(), reportId));
    }

    @PostMapping(value = "/report/{reportId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadIssueReportImage(@AuthenticationPrincipal User user,
                                                    @PathVariable Integer reportId,
                                                    @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(200).body(issueReportService.uploadIssueReportImage(user.getId(), reportId, image));
    }

    @GetMapping("/me/status/{status}")
    public ResponseEntity<?> getUserIssueReportsByStatus(@AuthenticationPrincipal User user,
                                                         @PathVariable String status) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportsByStatus(user.getId(), status));
    }

    @GetMapping("/me/priority/{priority}")
    public ResponseEntity<?> getUserIssueReportsByPriority(@AuthenticationPrincipal User user,
                                                           @PathVariable String priority) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportsByPriority(user.getId(), priority));
    }

    @GetMapping("/me/category/{category}")
    public ResponseEntity<?> getUserIssueReportsByCategory(@AuthenticationPrincipal User user,
                                                           @PathVariable String category) {
        return ResponseEntity.status(200).body(issueReportService.getUserIssueReportsByCategory(user.getId(), category));
    }

    @GetMapping("/me/search")
    public ResponseEntity<?> searchUserIssueReports(@AuthenticationPrincipal User user,
                                                    @RequestParam String keyword) {
        return ResponseEntity.status(200).body(issueReportService.searchUserIssueReports(user.getId(), keyword));
    }

    @GetMapping("/mayor-report/pdf")
    public ResponseEntity<?> generateMayorIssueReportPdf(@AuthenticationPrincipal User user) {
        byte[] pdfBytes = issueReportService.generateMayorIssueReportPdf(user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Neighborhood-Issue-Report.pdf\"");

        return ResponseEntity.status(200).headers(headers).body(pdfBytes);
    }

    @PostMapping("/mayor-report/pdf/email")
    public ResponseEntity<?> sendMayorIssueReportPdfEmail(@AuthenticationPrincipal User user) {
        issueReportService.sendMayorIssueReportPdfEmail(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Issue report PDF sent to mayor email"));
    }

    // ADMIN/DEBUG
    @GetMapping("/admin/neighborhood/{neighborhoodId}")
    public ResponseEntity<?> getIssueReportsByNeighborhood(@PathVariable Integer neighborhoodId) {
        return ResponseEntity.status(200).body(issueReportService.getIssueReportsByNeighborhood(neighborhoodId));
    }

    // ADMIN/DEBUG
    @GetMapping("/search")
    public ResponseEntity<?> searchIssueReports(@RequestParam String keyword) {
        return ResponseEntity.status(200).body(issueReportService.searchIssueReports(keyword));
    }

    @PutMapping("/{id}/start-progress")
    public ResponseEntity<?> startProgress(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        issueReportService.startProgress(id, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Issue report moved to in progress successfully"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeReport(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        issueReportService.completeReport(id, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Issue report completed successfully"));
    }

    // ADMIN/DEBUG
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        issueReportService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report deleted successfully"));
    }
}
