package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryInDTO {
    @NotBlank(message = "Subject cannot be empty")
    private String subject;

    @Pattern(regexp = "OPEN|RESOLVED", message = "Status must be OPEN or RESOLVED")
    private String status;
}
