package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Title cannot be null")
    @Size(min = 5, max = 100, message = "Length must be between 5 and 100 characters")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Description cannot be null")
    private String description;

    // Category and priority are assigned by AI based on title and description.
    @Column(nullable = false)
    @NotBlank(message = "Category cannot be null")
    @Pattern(regexp = "LIGHTING|ROADS|CLEANLINESS|VISUAL_POLLUTION|PARKS|WATER_AND_SEWAGE|ANIMALS|SAFETY|OTHER", message = "Category must be LIGHTING, ROADS, CLEANLINESS, VISUAL_POLLUTION, PARKS, WATER_AND_SEWAGE, ANIMALS, SAFETY or OTHER only")
    private String category;

    // Category and priority are assigned by AI based on title and description.
    @Column(nullable = false)
    @NotBlank(message = "Priority cannot be null")
    @Pattern(regexp = "URGENT|NON_URGENT|PERIODIC", message = "Priority must be URGENT, NON_URGENT or PERIODIC only")
    private String priority;

    @Column(nullable = false)
    @NotBlank(message = "Status cannot be null")
    @Pattern(regexp = "OPEN|IN_PROGRESS|COMPLETED", message = "Status must be OPEN, IN_PROGRESS or COMPLETED only")
    private String status = "OPEN";

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @CurrentTimestamp
    private LocalDateTime createdAt;

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

    @OneToOne(mappedBy = "issueReport", cascade = CascadeType.ALL)
    private AIModeration aiModeration;

}
