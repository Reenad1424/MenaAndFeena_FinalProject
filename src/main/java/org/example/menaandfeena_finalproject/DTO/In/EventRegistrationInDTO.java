package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventRegistrationInDTO {

    @NotNull(message = "User id cannot be null")
    private Integer userId;

    @NotNull(message = "Event id cannot be null")
    private Integer eventId;

    private Integer familyMemberId;
}
