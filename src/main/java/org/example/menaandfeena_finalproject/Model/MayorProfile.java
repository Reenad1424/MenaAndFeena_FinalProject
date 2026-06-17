package org.example.menaandfeena_finalproject.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MayorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "ACTIVE|INACTIVE",
            message = "Status must be ACTIVE or INACTIVE")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @OneToOne
//    @JoinColumn(name = "neighborhood_id")
//    private Neighborhood neighborhood;



}
