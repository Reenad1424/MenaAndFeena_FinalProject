package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter @Getter
public class MayorVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "datetime not null")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;




    @ManyToOne
    @JoinColumn(name = "mayor_candidate_id", referencedColumnName = "id")
    private MayorCandidate mayorCandidate;

    @ManyToOne
    @JoinColumn(name = "election_round_id", referencedColumnName = "id")
    private ElectionRound electionRound;
}
