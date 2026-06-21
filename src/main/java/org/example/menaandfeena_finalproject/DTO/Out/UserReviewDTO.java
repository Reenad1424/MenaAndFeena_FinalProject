package org.example.menaandfeena_finalproject.DTO.Out;
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

    private LocalDate createdAt;

    private String reviewerName;

}