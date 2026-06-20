package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.Out.NeighborhoodDashboardDTO;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Service.NeighborhoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;@RestController
@RequestMapping("/api/v1/neighborhoods")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;


    // =========================
    // GET ALL NEIGHBORHOODS
    // =========================

    @GetMapping
    public ResponseEntity<List<Neighborhood>> getAllNeighborhoods() {

        return ResponseEntity.ok(
                neighborhoodService.getAllNeighborhoods()
        );
    }


    // =========================
    // CREATE NEIGHBORHOOD
    // =========================

    @PostMapping
    public ResponseEntity<ApiResponse> createNeighborhood(
            @RequestBody @Valid Neighborhood neighborhood
    ) {

        neighborhoodService.createNeighborhood(neighborhood);

        return ResponseEntity.status(201).body(
                new ApiResponse("Neighborhood added successfully")
        );
    }


    // =========================
    // UPDATE NEIGHBORHOOD
    // =========================

    @PutMapping("/{neighborhoodId}")
    public ResponseEntity<ApiResponse> updateNeighborhood(
            @PathVariable Integer neighborhoodId,
            @RequestBody @Valid Neighborhood neighborhood
    ) {

        neighborhoodService.updateNeighborhood(
                neighborhoodId,
                neighborhood
        );

        return ResponseEntity.ok(
                new ApiResponse("Neighborhood updated successfully")
        );
    }


    // =========================
    // DELETE NEIGHBORHOOD
    // =========================

    @DeleteMapping("/{neighborhoodId}")
    public ResponseEntity<ApiResponse> deleteNeighborhood(
            @PathVariable Integer neighborhoodId
    ) {

        neighborhoodService.deleteNeighborhood(neighborhoodId);

        return ResponseEntity.ok(
                new ApiResponse("Neighborhood deleted successfully")
        );
    }


    // =========================
    // GET NEIGHBORHOOD DASHBOARD BY USER
    // =========================

    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<NeighborhoodDashboardDTO> getNeighborhoodDashboardByUser(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.ok(
                neighborhoodService.getNeighborhoodDashboardByUser(userId)
        );
    }
}