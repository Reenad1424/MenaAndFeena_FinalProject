package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MayorProfileInDTO {

    @NotNull(message = "User id cannot be null")
    private Integer userId;

    @NotNull(message = "Neighborhood id cannot be null")
    private Integer neighborhoodId;
}
