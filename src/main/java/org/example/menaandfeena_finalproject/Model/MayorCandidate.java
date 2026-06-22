package org.example.menaandfeena_finalproject.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@Setter @Getter
public class MayorCandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "datetime not null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedAt;

    // PENDING, APPROVED, REJECTED
    @Column(columnDefinition = "varchar(20) not null")
    private String status;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;


    @OneToMany(mappedBy = "mayorCandidate", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MayorVote> mayorVotes;

    @ManyToOne
    @JoinColumn(name = "election_round_id", referencedColumnName = "id")
    @JsonIgnore
    private ElectionRound electionRound;
}
