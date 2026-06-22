package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewDTO {

    private Integer id;

    private Integer rating;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private String reviewerName;

}