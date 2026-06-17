package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.example.menaandfeena_finalproject.Service.MayorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mayor-profile")
@RequiredArgsConstructor
public class MayorProfileController {
    private final MayorProfileService mayorProfileService;

    @GetMapping("/get")
    public ResponseEntity getAllMayorProfiles() {
        return ResponseEntity.status(200)
                .body(mayorProfileService.getAllMayorProfiles());
    }

    @PostMapping("/add")
    public ResponseEntity addMayorProfile(@Valid @RequestBody MayorProfile mayorProfile) {

        mayorProfileService.addMayorProfile(mayorProfile);

        return ResponseEntity.status(200).body(new ApiResponse("Mayor profile added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateMayorProfile(@PathVariable Integer id, @Valid @RequestBody MayorProfile mayorProfile) {

        mayorProfileService.updateMayorProfile(id, mayorProfile);

        return ResponseEntity.status(200).body(new ApiResponse("Mayor profile updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteMayorProfile(@PathVariable Integer id) {

        mayorProfileService.deleteMayorProfile(id);

        return ResponseEntity.status(200).body(new ApiResponse("Mayor profile deleted successfully"));
    }
}
