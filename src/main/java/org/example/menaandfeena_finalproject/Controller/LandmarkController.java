package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.LandmarkInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.LandmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/landmarks")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllLandmarks() {
        return ResponseEntity.status(200).body(
                landmarkService.getAllLandmarks()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> createLandmark(
            @RequestBody @Valid LandmarkInDTO landmarkInDTO
    ) {
        landmarkService.createLandmark(landmarkInDTO);

        return ResponseEntity.status(201).body(
                new ApiResponse("تمت إضافة المعلم بنجاح")
        );
    }

    @PutMapping("/update/{landmarkId}")
    public ResponseEntity<?> updateLandmark(
            @PathVariable Integer landmarkId,
            @RequestBody @Valid LandmarkInDTO landmarkInDTO
    ) {
        landmarkService.updateLandmark(landmarkId, landmarkInDTO);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث المعلم بنجاح")
        );
    }

    @DeleteMapping("/delete/{landmarkId}")
    public ResponseEntity<?> deleteLandmark(
            @PathVariable Integer landmarkId
    ) {
        landmarkService.deleteLandmark(landmarkId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم حذف المعلم بنجاح")
        );
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncLandmarksForCurrentUser(
            @AuthenticationPrincipal User user,
            @RequestParam Integer radius
    ) {
        int savedCount =
                landmarkService.syncLandmarksForUser(
                        user.getId(),
                        radius
                );

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث معالم الحي بنجاح، عدد المعالم المحفوظة: " + savedCount)
        );
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyLandmarksForCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(200).body(
                landmarkService.getNearbyLandmarksForUser(user.getId())
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getLandmarkDashboard(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(200).body(
                landmarkService.getLandmarkDashboard(user.getId())
        );
    }
}