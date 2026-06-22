package org.example.menaandfeena_finalproject.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// سجل تتبّع البلاغ (Timeline). كل سطر يمثّل خطوة تاريخية في مسار البلاغ (شبيه بتتبّع الشحنات).
// هذا السجل للتاريخ/التتبّع فقط ولا يحل محل IssueReport.status الذي يخزّن الحالة الحالية.
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IssueReportTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // المرحلة (مثل REPORT_CREATED, AI_CLASSIFIED, IN_PROGRESS, COMPLETED).
    @Column(nullable = false)
    private String stage;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // نخزّن بدقّة الميكروثانية حتى يبقى ترتيب الخطوات صحيحاً حتى لو أُضيفت في نفس اللحظة.
    @Column(columnDefinition = "datetime(6)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_report_id", nullable = false)
    @JsonIgnore
    private IssueReport issueReport;
}
