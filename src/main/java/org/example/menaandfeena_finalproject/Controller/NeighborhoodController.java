package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.NeighborhoodInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.NeighborhoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/neighborhoods")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllNeighborhoods() {
        return ResponseEntity.status(200).body(
                neighborhoodService.getAllNeighborhoods()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> createNeighborhood(
            @RequestBody @Valid NeighborhoodInDTO neighborhoodInDTO
    ) {
        neighborhoodService.createNeighborhood(neighborhoodInDTO);

        return ResponseEntity.status(201).body(
                new ApiResponse("Neighborhood added successfully")
        );
    }

    @PutMapping("/update/{neighborhoodId}")
    public ResponseEntity<?> updateNeighborhood(
            @PathVariable Integer neighborhoodId,
            @RequestBody @Valid NeighborhoodInDTO neighborhoodInDTO
    ) {
        neighborhoodService.updateNeighborhood(
                neighborhoodId,
                neighborhoodInDTO
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Neighborhood updated successfully")
        );
    }

    @DeleteMapping("/delete/{neighborhoodId}")
    public ResponseEntity<?> deleteNeighborhood(
            @PathVariable Integer neighborhoodId
    ) {
        neighborhoodService.deleteNeighborhood(neighborhoodId);

        return ResponseEntity.status(200).body(
                new ApiResponse("Neighborhood deleted successfully")
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getNeighborhoodDashboardByCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(200).body(
                neighborhoodService.getNeighborhoodDashboardByUser(user.getId())
        );
    }
}