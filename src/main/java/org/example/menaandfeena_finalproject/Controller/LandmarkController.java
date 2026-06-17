package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Landmark;
import org.example.menaandfeena_finalproject.Service.LandmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/landmark")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Landmark>> getAll() {
        return ResponseEntity.status(200).body(landmarkService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody @Valid Landmark landmark) {
        landmarkService.add(landmark);
        return ResponseEntity.status(201).body(new ApiResponse("Landmark added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody @Valid Landmark landmark) {
        landmarkService.update(id, landmark);
        return ResponseEntity.status(200).body(new ApiResponse("Landmark updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        landmarkService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Landmark deleted successfully"));
    }
}
