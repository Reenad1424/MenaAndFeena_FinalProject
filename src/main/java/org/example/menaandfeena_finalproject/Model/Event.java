package org.example.menaandfeena_finalproject.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(100) not null")
    private String title;


    @Column(columnDefinition = "varchar(500) not null")
    private String description;


    private LocalDateTime date;


    @Column(columnDefinition = "varchar(150) not null")
    private String location;

    @Column(columnDefinition = "boolean not null")
    private Boolean isPaid;


    private Double price;


    private Integer maxParticipants;


    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<EventRegistration> registrations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id")
    private Neighborhood neighborhood;


    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
