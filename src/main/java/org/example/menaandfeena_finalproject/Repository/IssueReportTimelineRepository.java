package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.IssueReportTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueReportTimelineRepository extends JpaRepository<IssueReportTimeline, Integer> {

    // كل خطوات تتبّع البلاغ مرتبة تصاعدياً حسب وقت الإنشاء.
    List<IssueReportTimeline> findByIssueReport_IdOrderByCreatedAtAsc(Integer issueReportId);

    // نستخدمها لتجنّب تكرار نفس المرحلة لنفس البلاغ.
    boolean existsByIssueReport_IdAndStage(Integer issueReportId, String stage);
}
