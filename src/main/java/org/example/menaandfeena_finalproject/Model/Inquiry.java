package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Subject cannot be empty")
    @Column(columnDefinition = "varchar(150) not null")
    private String subject;

    @Pattern(regexp = "OPEN|RESOLVED", message = "Status must be OPEN or RESOLVED")
    @Column(columnDefinition = "varchar(20) not null")
    private String status = "OPEN";

    @Column(columnDefinition = "datetime not null")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "target_user_id", referencedColumnName = "id")
    private User targetUser;

    @ManyToOne
    @JoinColumn(name = "marketplace_item_id", referencedColumnName = "id")
    private MarketPlaceItem marketPlaceItem;

    @ManyToOne
    @JoinColumn(name = "announcement_id", referencedColumnName = "id")
    private Announcement announcement;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<InquiryMessage> messages;
}
