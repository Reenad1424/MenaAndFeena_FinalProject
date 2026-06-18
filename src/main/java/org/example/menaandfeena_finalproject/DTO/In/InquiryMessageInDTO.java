package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryMessageInDTO {

    @NotBlank(message = "Message content cannot be blank")
    @Size(max = 500, message = "Message content must not exceed 500 characters")
    private String content;
}
