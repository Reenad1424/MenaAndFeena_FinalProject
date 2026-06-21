package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeighborhoodResidentDTO {
    private Integer userId;
    private String fullName;
    private String gender;
    private String phone;
}