package org.example.menaandfeena_finalproject.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Title cannot be empty")
    @Column(columnDefinition = "varchar(100) not null")
    private String title;



    @NotEmpty(message = "Content cannot be empty")
    @Column(columnDefinition = "varchar(500) not null")
    private String content;

    @Pattern(regexp = "PENDING|APPROVED|REJECTED", message = "Status must be PENDING, APPROVED, or REJECTED")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;


    @NotNull(message = "Created date cannot be null")
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

}
