package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String category;

    private String status;

}