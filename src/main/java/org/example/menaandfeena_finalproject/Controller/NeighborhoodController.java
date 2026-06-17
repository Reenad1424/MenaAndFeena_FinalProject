package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Service.NeighborhoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/neighborhood")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Neighborhood>> getAll() {
        return ResponseEntity.status(200).body(neighborhoodService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody @Valid Neighborhood neighborhood) {
        neighborhoodService.add(neighborhood);
        return ResponseEntity.status(201).body(new ApiResponse("Neighborhood added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody @Valid Neighborhood neighborhood) {
        neighborhoodService.update(id, neighborhood);
        return ResponseEntity.status(200).body(new ApiResponse("Neighborhood updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        neighborhoodService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Neighborhood deleted successfully"));
    }
}
