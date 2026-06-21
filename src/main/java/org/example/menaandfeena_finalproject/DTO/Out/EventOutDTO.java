package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventOutDTO {

    private Integer eventid;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private Boolean isPaid;
    private Double price;
    private Integer maxParticipants;
    private String status;

}