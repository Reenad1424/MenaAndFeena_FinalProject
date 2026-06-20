package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.*;@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LandmarkResponseDto {

    private Integer id;

    private String name;

    private String type;

    private Long distanceMeters;
}