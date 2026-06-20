package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIssueDTO {

    private Integer id;

    private String title;

    private String category;

    private String priority;

    private String status;

    private LocalDateTime createdAt;

}