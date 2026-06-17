package org.example.menaandfeena_finalproject.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

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
    private LocalDateTime date;


    @NotEmpty(message = "Location cannot be empty")
    @Column(columnDefinition = "varchar(150) not null")
    private String location;

    @NotNull(message = "Paid status cannot be null")
    @Column(columnDefinition = "boolean not null")
    private Boolean isPaid;


    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;


    @NotNull(message = "Max participants cannot be null")
    @Positive(message = "Max participants must be positive")
    private Integer maxParticipants;


    @Pattern(regexp = "ACTIVE|CANCELLED|COMPLETED",
            message = "Status must be ACTIVE, CANCELLED, or COMPLETED")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;



}
