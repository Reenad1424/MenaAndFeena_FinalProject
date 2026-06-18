package org.example.menaandfeena_finalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a valid email format")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    @Column(unique = true)
    private String nationalId;

    private Date birthDate;

    @Column
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be either MALE or FEMALE only")
    private String gender;

    @Column
    @Pattern(regexp = "RESIDENT|MAYOR", message = "Status must be either RESIDENT or MAYOR only")
    private String status;

    private Integer yearsInNeighborhood;

    private Boolean isVerified = false;

    private Date createdAt = new Date();

    // علاقة أفراد العائلة (One User to Many Family Members)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FamilyMember> familyMembers;

    // علاقة السكن داخل حي (Many Users to One Neighborhood)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id")
    private Neighborhood neighborhood;

    // علاقة البلاغات المرفوعة (One User to Many Issue Reports)
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL)
    private List<IssueReport> issueReports;

    // دالة احتساب العمر تلقائياً في الجافا من تاريخ الميلاد
    public Integer getAge() {
        if (this.birthDate == null) return null;
        long timeInMilli = new Date().getTime() - this.birthDate.getTime();
        return (int) (timeInMilli / (1000L * 60 * 60 * 24 * 365));
    }
}
