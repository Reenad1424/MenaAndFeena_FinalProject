package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FamilyMemberInDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 60, message = "Age must be realistic")
    private Integer age;

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be either MALE or FEMALE")
    private String gender;

    @NotBlank(message = "Relation cannot be blank")
    private String relation;
}
