package org.example.menaandfeena_finalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Neighborhood name cannot be null")
    @Size(min = 3, max = 50, message = "Length must be between 3 and 50 characters")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "City cannot be null")
    private String city;

    @Min(value = 0, message = "Population cannot be negative")
    private Integer estimatedPopulation;

    @Min(value = 0, message = "Population cannot be negative")
    private Integer registeredPopulation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // ربط علاقة الحي بالمستخدمين
    @OneToMany(mappedBy = "neighborhood", cascade = CascadeType.ALL)
    private List<User> residents;

    // ربط علاقة الحي بالمعالم السكنية
    @OneToMany(mappedBy = "neighborhood", cascade = CascadeType.ALL)
    private List<Landmark> landmarks;
}
