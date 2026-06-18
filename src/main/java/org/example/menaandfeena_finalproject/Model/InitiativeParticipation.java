package org.example.menaandfeena_finalproject.Model;


import jakarta.persistence.*;
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
public class InitiativeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Pattern(regexp = "JOINED|LEFT|CONFIRMED", message = "Status must be JOINED, LEFT, or CONFIRMED")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @NotNull(message = "Joined date cannot be null")
    private LocalDate joinedAt;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "initiative_id")
    private Initiative initiative;




}
