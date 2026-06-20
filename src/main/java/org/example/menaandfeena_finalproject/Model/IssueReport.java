package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "issue_reports")
public class IssueReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Title, category, and priority are assigned by AI based on the user's description.
    @Column(nullable = false)
    private String category;

    // Title, category, and priority are assigned by AI based on the user's description.
    @Column(nullable = false)
    private String priority;

    @Column(nullable = false)
    private String status = "OPEN";

    private Double latitude;

    private Double longitude;

    @CurrentTimestamp
    private LocalDateTime createdAt;

    @Column
    private String reportedStreetName;

    @Column
    private String detectedDistrictName;

    @Column
    private String detectedStreetName;

    @Column
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_neighborhood_id", nullable = false)
    private Neighborhood reportNeighborhood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User reporter; // الجار المبلّغ

}
