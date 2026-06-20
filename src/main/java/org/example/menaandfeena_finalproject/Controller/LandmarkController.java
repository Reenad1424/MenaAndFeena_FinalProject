package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.Out.LandmarkResponseDto;
import org.example.menaandfeena_finalproject.DTO.Out.LandmarkDashboardDto;
import org.example.menaandfeena_finalproject.Model.Landmark;
import org.example.menaandfeena_finalproject.Service.LandmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;@RestController
@RequestMapping("/api/v1/landmarks")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;


    @GetMapping
    public ResponseEntity<List<Landmark>> getAllLandmarks() {

        return ResponseEntity.ok(
                landmarkService.getAllLandmarks()
        );
    }


    @PostMapping
    public ResponseEntity<ApiResponse> createLandmark(
            @RequestBody @Valid Landmark landmark
    ) {

        landmarkService.createLandmark(landmark);

        return ResponseEntity.status(201).body(
                new ApiResponse("تمت إضافة المعلم بنجاح")
        );
    }


    @PutMapping("/{landmarkId}")
    public ResponseEntity<ApiResponse> updateLandmark(
            @PathVariable Integer landmarkId,
            @RequestBody @Valid Landmark landmark
    ) {

        landmarkService.updateLandmark(
                landmarkId,
                landmark
        );

        return ResponseEntity.ok(
                new ApiResponse("تم تحديث المعلم بنجاح")
        );
    }


    @DeleteMapping("/{landmarkId}")
    public ResponseEntity<ApiResponse> deleteLandmark(
            @PathVariable Integer landmarkId
    ) {

        landmarkService.deleteLandmark(landmarkId);

        return ResponseEntity.ok(
                new ApiResponse("تم حذف المعلم بنجاح")
        );
    }


    @PostMapping("/sync/user/{userId}")
    public ResponseEntity<ApiResponse> syncLandmarksForUser(
            @PathVariable Integer userId,
            @RequestParam Integer radius
    ) {

        landmarkService.syncLandmarksForUser(
                userId,
                radius
        );

        return ResponseEntity.ok(
                new ApiResponse("تم تحديث معالم الحي بنجاح")
        );
    }


    @GetMapping("/user/{userId}/nearby")
    public ResponseEntity<List<LandmarkResponseDto>> getNearbyLandmarksForUser(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.ok(
                landmarkService.getNearbyLandmarksForUser(userId)
        );
    }


    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<LandmarkDashboardDto> getLandmarkDashboard(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.ok(
                landmarkService.getLandmarkDashboard(userId)
        );
    }
}