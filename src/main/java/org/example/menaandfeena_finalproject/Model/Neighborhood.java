package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "neighborhoods")
public class Neighborhood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    private Integer estimatedPopulation;
    private Integer registeredPopulation;
    private Date createdAt = new Date();

    // 🌟 حقول إحداثيات مركز الحي لتحديد أقرب حي لليوزر
    private Double latitude;

    private Double longitude;

    // علاقة الحي مع السكان
    @OneToMany(mappedBy = "neighborhood", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> residents;

    // 🌟 تم تفعيل وتصحيح العلاقة مع المعالم المتواجدة في الحي
    @OneToMany(mappedBy = "neighborhood", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Landmark> landmarks;
}
