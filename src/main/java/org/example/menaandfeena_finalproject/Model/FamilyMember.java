package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be null")
    @Size(min = 2, max = 30, message = "Length must be between 2 and 30 characters")
    private String name;

    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 130, message = "Age must be realistic")
    private Integer age;

    @Column(nullable = false)
    @NotBlank(message = "Gender cannot be null")
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be either MALE or FEMALE only")
    private String gender;

    @Column(nullable = false)
    @NotBlank(message = "Relation cannot be null")
    private String relation; // ابن، ابنة، زوجة

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}
