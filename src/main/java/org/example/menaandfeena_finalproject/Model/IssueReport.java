package org.example.menaandfeena_finalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Date;

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

    @Column(nullable = false)
    @NotBlank(message = "Type cannot be null")
    @Pattern(regexp = "INFRASTRUCTURE|CLEANLINESS|OTHER", message = "Type must be INFRASTRUCTURE, CLEANLINESS or OTHER only")
    private String type;

    @Column(nullable = false)
    @NotBlank(message = "Category cannot be null")
    @Pattern(regexp = "URGENT|NON_URGENT|PERIODIC", message = "Category must be URGENT, NON_URGENT or PERIODIC only")
    private String category;

    @Column(nullable = false)
    @NotBlank(message = "Status cannot be null")
    @Pattern(regexp = "OPEN|IN_PROGRESS|COMPLETED", message = "Status must be OPEN, IN_PROGRESS or COMPLETED only")
    private String status = "OPEN";

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    private Date createdAt = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User reporter; // الجار المبلّغ
}
