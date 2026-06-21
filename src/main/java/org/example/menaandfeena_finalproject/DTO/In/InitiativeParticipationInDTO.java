package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitiativeParticipationInDTO {

    @NotNull(message = "User id cannot be null")
    private Integer userId;

    @NotNull(message = "Initiative id cannot be null")
    private Integer initiativeId;
}
