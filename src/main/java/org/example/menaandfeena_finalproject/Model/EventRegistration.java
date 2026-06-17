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
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Pattern(regexp = "PENDING|CONFIRMED|CANCELLED", message = "Status must be PENDING, CONFIRMED, or CANCELLED")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;


    @NotNull(message = "Registration date cannot be null")
    private LocalDate registeredAt;


   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;



}
