package org.example.menaandfeena_finalproject.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(150) not null")
    private String subject;

    @Column(columnDefinition = "varchar(20) not null")
    private String status = "OPEN";

    @Column(columnDefinition = "datetime not null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
