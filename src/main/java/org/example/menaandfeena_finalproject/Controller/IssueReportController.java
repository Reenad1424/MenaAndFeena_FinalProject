package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Service.IssueReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/issue-report")
@RequiredArgsConstructor
public class IssueReportController {

    private final IssueReportService issueReportService;

    @GetMapping("/get-all")
    public ResponseEntity<List<IssueReport>> getAll() {
        return ResponseEntity.status(200).body(issueReportService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody @Valid IssueReport issueReport) {
        issueReportService.add(issueReport);
        return ResponseEntity.status(201).body(new ApiResponse("Issue report submitted successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody @Valid IssueReport issueReport) {
        issueReportService.update(id, issueReport);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        issueReportService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Issue report deleted successfully"));
    }
}
