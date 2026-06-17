package org.example.menaandfeena_finalproject.DTO.In;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MayorVoteInDTO {
    private LocalDateTime createdAt;
}
