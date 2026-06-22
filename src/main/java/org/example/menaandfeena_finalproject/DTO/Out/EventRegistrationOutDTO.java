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
public class EventRegistrationOutDTO {

    private Integer registrationid;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registeredAt;

    private Integer userId;
    private String userFullName;

    private Integer eventId;
    private String eventTitle;
    private Boolean isPaid;
    private Double price;

}