package org.example.menaandfeena_finalproject.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "varchar(100) not null")
    private String title;

    @Column(columnDefinition = "varchar(500) not null")
    private String description;

    private LocalDate date;

    private Integer maxParticipants;

    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @OneToMany(mappedBy = "initiative", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<InitiativeParticipation> participations;

    @OneToMany(mappedBy = "initiative", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Review> reviews;



    @Column(columnDefinition = "varchar(30) not null")
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id")
    private Neighborhood neighborhood;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
}
