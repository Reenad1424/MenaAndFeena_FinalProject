package org.example.menaandfeena_finalproject.DTO.In;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;


@Data
public class UserRegisterRequestDto {
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a valid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone number cannot be null")
    private String phone;

    @NotBlank(message = "National ID cannot be null")
    private String nationalId;

    @NotNull(message = "Birth date cannot be null")
    private LocalDate birthDate;

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be either MALE or FEMALE only")
    private String gender;

    private Integer yearsInNeighborhood;

    private Double latitude;
    private Double longitude;

}
