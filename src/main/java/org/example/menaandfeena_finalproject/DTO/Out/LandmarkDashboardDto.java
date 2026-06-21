package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandmarkDashboardDto {

    private Integer userId;

    private String neighborhoodName;

    private LandmarkItem nearestMosque;

    private LandmarkItem nearestSchool;

    private LandmarkItem nearestPark;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LandmarkItem {

        private String name;

        private Long distanceMeters;
    }
}