package org.example.menaandfeena_finalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ai_moderations")
public class AIModeration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Status cannot be null")
    @Pattern(regexp = "APPROVED|REJECTED", message = "Status must be either APPROVED or REJECTED only")
    private String status;

    @Column(nullable = false)
    @NotBlank(message = "Reason cannot be null")
    private String reason;

    @NotNull(message = "Confidence score cannot be null")
    @Min(value = 0, message = "Score must be at least 0.0")
    @Max(value = 1, message = "Score cannot exceed 1.0")
    private Double confidenceScore;
}
