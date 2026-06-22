package org.example.menaandfeena_finalproject.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// كيان الميزات المعرّفة مسبقاً للفعاليات (مثل: مناسبة للعائلات، فعالية ثقافية...).
// هذه الميزات يتم زرعها مسبقاً في قاعدة البيانات، ويختار المنظّم منها عند إنشاء الفعالية.
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(50) not null", unique = true)
    private String name;
}
