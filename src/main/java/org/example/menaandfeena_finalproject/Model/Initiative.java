package org.example.menaandfeena_finalproject.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Initiative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Title cannot be empty")
    @Column(columnDefinition = "varchar(100) not null")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    @Column(columnDefinition = "varchar(500) not null")
    private String description;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Max participants cannot be null")
    @Positive(message = "Max participants must be positive")
    private Integer maxParticipants;

    @Pattern(regexp = "ACTIVE|COMPLETED|CANCELLED", message = "Status must be ACTIVE, COMPLETED, or CANCELLED")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @OneToMany(mappedBy = "initiative", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<InitiativeParticipation> participations;

    @OneToMany(mappedBy = "initiative", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Review> reviews;



    @NotEmpty(message = "Category cannot be empty")
    @Pattern(regexp = "ENVIRONMENTAL|VOLUNTEERING|EDUCATIONAL|HEALTH|SPORTS", message = "Category must be ENVIRONMENTAL, "
            + "VOLUNTEERING, EDUCATIONAL, HEALTH, or SPORTS")
    @Column(columnDefinition = "varchar(30) not null")
    private String category;

}
