package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementOutDTO {

    private Integer id;
    private String title;
    private String content;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
    private String publisherName;

}