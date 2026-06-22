package org.example.menaandfeena_finalproject.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

// كيان جدول/برنامج الفعالية. كل سطر يمثل فقرة في برنامج الفعالية (وقت + عنوان + ترتيب).
// المنظّم يدخل هذه الفقرات يدوياً، والباك إند يخزّنها ويرجعها مرتبة فقط.
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "time not null")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    @Column(columnDefinition = "varchar(150) not null")
    private String title;

    @Column(columnDefinition = "int not null")
    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;
}
