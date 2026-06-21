package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInitiativeDTO {

    private Integer id;

    private String title;

    private LocalDate date;

    private String category;

    private String status;

}