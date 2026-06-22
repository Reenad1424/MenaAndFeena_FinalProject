package org.example.menaandfeena_finalproject.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
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


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    // وقت نهاية الفعالية (اختياري). يُستخدم للتحقق أن فقرات البرنامج تقع ضمن وقت الفعالية.
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    @Column(columnDefinition = "varchar(150) not null")
    private String location;

    @Column(columnDefinition = "boolean not null")
    private Boolean isPaid;


    // للفعاليات المجانية يكون السعر null فلا يظهر في الرد (JSON).
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double price;


    private Integer maxParticipants;


    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @Column(columnDefinition = "varchar(500)")
    private String imageUrl;

    // الميزات المختارة لهذه الفعالية من قائمة الميزات المعرّفة مسبقاً (اختيار متعدد).
    // EAGER حتى تظهر الميزات مباشرة ضمن بيانات الفعالية في الرد JSON.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_features",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<EventFeature> features;

    // فقرات برنامج الفعالية. مخفية من JSON المباشر للفعالية وتُعرض عبر endpoint مخصص مرتبة.
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EventSchedule> schedule;

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
