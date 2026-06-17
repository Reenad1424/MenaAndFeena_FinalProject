package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "landmarks")
public class Landmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Landmark name cannot be null")
    @Size(min = 2, max = 50, message = "Length must be between 2 and 50 characters")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Type cannot be null")
    @Pattern(regexp = "MOSQUE|SCHOOL|PARK|HOSPITAL|OTHER", message = "Type must be MOSQUE, SCHOOL, PARK, HOSPITAL or OTHER only")
    private String type;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    @JsonIgnore
    private Neighborhood neighborhood;
}
