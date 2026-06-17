package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@NoArgsConstructor
@Setter @Getter
public class ElectionRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "date not null")
    private LocalDate startDate;

    @Column(columnDefinition = "date not null")
    private LocalDate endDate;

    // ACTIVE, CLOSED
    @Column(columnDefinition = "varchar(10) not null")
    private String status;

    @OneToMany(mappedBy = "electionRound", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MayorCandidate> mayorCandidates;

    @OneToMany(mappedBy = "electionRound", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MayorVote> mayorVotes;
}
