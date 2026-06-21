package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.NeighborhoodInDTO;
import org.example.menaandfeena_finalproject.Service.NeighborhoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/neighborhoods")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;


    // =========================
    // GET ALL NEIGHBORHOODS
    // =========================

    @GetMapping
    public ResponseEntity<?> getAllNeighborhoods() {

        return ResponseEntity.status(200).body(
                neighborhoodService.getAllNeighborhoods()
        );
    }


    // =========================
    // CREATE NEIGHBORHOOD
    // =========================

    @PostMapping
    public ResponseEntity<?> createNeighborhood(
            @RequestBody @Valid NeighborhoodInDTO neighborhoodInDTO
    ) {

        neighborhoodService.createNeighborhood(neighborhoodInDTO);

        return ResponseEntity.status(200).body(
                new ApiResponse("Neighborhood added successfully")
        );
    }


    // =========================
    // UPDATE NEIGHBORHOOD
    // =========================

    @PutMapping("/{neighborhoodId}")
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


    // =========================
    // DELETE NEIGHBORHOOD
    // =========================

    @DeleteMapping("/{neighborhoodId}")
    public ResponseEntity<?> deleteNeighborhood(
            @PathVariable Integer neighborhoodId
    ) {

        neighborhoodService.deleteNeighborhood(neighborhoodId);

        return ResponseEntity.status(200).body(
                new ApiResponse("Neighborhood deleted successfully")
        );
    }


    // =========================
    // GET NEIGHBORHOOD DASHBOARD BY USER
    // =========================

    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<?> getNeighborhoodDashboardByUser(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.status(200).body(
                neighborhoodService.getNeighborhoodDashboardByUser(userId)
        );
    }
}
