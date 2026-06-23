package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventOutDTO {

    private Integer id;
    private String title;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;
    private String location;
    private Boolean isPaid;
    private Integer maxParticipants;
    private String status;
    private List<String> features;
    private String neighborhoodName;
    private String creatorName;

}
