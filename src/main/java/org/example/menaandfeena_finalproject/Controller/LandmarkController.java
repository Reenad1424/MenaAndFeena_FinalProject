package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.LandmarkInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.LandmarkResponseDto;
import org.example.menaandfeena_finalproject.DTO.Out.LandmarkDashboardDto;
import org.example.menaandfeena_finalproject.Service.LandmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;@RestController
@RequestMapping("/api/v1/landmarks")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;


    @GetMapping
    public ResponseEntity<?> getAllLandmarks() {

        return ResponseEntity.status(200).body(
                landmarkService.getAllLandmarks()
        );
    }


    @PostMapping
    public ResponseEntity<?> createLandmark(
            @RequestBody @Valid LandmarkInDTO landmarkInDTO
    ) {

        landmarkService.createLandmark(landmarkInDTO);

        return ResponseEntity.status(200).body(
                new ApiResponse("تمت إضافة المعلم بنجاح")
        );
    }


    @PutMapping("/{landmarkId}")
    public ResponseEntity<?> updateLandmark(
            @PathVariable Integer landmarkId,
            @RequestBody @Valid LandmarkInDTO landmarkInDTO
    ) {

        landmarkService.updateLandmark(
                landmarkId,
                landmarkInDTO
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث المعلم بنجاح")
        );
    }


    @DeleteMapping("/{landmarkId}")
    public ResponseEntity<?> deleteLandmark(
            @PathVariable Integer landmarkId
    ) {

        landmarkService.deleteLandmark(landmarkId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم حذف المعلم بنجاح")
        );
    }


    @PostMapping("/sync/user/{userId}")
    public ResponseEntity<?> syncLandmarksForUser(
            @PathVariable Integer userId,
            @RequestParam Integer radius
    ) {

        landmarkService.syncLandmarksForUser(
                userId,
                radius
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث معالم الحي بنجاح")
        );
    }


    @GetMapping("/user/{userId}/nearby")
    public ResponseEntity<?> getNearbyLandmarksForUser(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.status(200).body(
                landmarkService.getNearbyLandmarksForUser(userId)
        );
    }


    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<?> getLandmarkDashboard(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.status(200).body(
                landmarkService.getLandmarkDashboard(userId)
        );
    }
}
