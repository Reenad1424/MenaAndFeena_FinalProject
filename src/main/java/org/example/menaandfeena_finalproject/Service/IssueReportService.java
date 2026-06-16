package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Repository.IssueReportRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueReportService {
    private final IssueReportRepository issueReportRepository;

    public List<IssueReport> getAll() {
        return issueReportRepository.findAll();
    }

    public void add(IssueReport issueReport) {
        issueReportRepository.save(issueReport);
    }

    public void update(Integer id, IssueReport issueReport) {
        IssueReport old = issueReportRepository.findIssueReportById(id);
        if (old == null) throw new ApiException("Issue report not found");
        old.setTitle(issueReport.getTitle());
        old.setDescription(issueReport.getDescription());
        old.setType(issueReport.getType());
        old.setCategory(issueReport.getCategory());
        old.setStatus(issueReport.getStatus());
        old.setLatitude(issueReport.getLatitude());
        old.setLongitude(issueReport.getLongitude());
        issueReportRepository.save(old);
    }

    public void delete(Integer id) {
        IssueReport issueReport = issueReportRepository.findIssueReportById(id);
        if (issueReport == null) throw new ApiException("Issue report not found");
        issueReportRepository.delete(issueReport);
    }
}
