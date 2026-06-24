package org.example.menaandfeena_finalproject.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.InitiativeInDTO;
import org.example.menaandfeena_finalproject.Service.InitiativeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.example.menaandfeena_finalproject.Model.User;

@RestController
@RequestMapping("/api/v1/initiative")
@RequiredArgsConstructor
public class InitiativeController {

    private final InitiativeService initiativeService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllInitiatives() {
        return ResponseEntity.status(200).body(initiativeService.getAllInitiatives());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateInitiative(@PathVariable Integer id,
                                           @Valid @RequestBody InitiativeInDTO initiativeInDTO) {
        initiativeService.updateInitiative(id, initiativeInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Initiative updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInitiative(@PathVariable Integer id) {
        initiativeService.deleteInitiative(id);
        return ResponseEntity.status(200).body(new ApiResponse("Initiative deleted successfully"));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getInitiativesByCategory(@PathVariable String category) {
        return ResponseEntity.status(200).body(initiativeService.getInitiativesByCategory(category));
    }


    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingInitiatives() {
        return ResponseEntity.status(200).body(initiativeService.getUpcomingInitiatives());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getInitiativeById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(initiativeService.getInitiativeById(id));

    }
    @PostMapping("/create")
    public ResponseEntity<?> createInitiative(Authentication authentication,
                                              @Valid @RequestBody InitiativeInDTO initiativeInDTO) {

        User user = (User) authentication.getPrincipal();

        initiativeService.createInitiative(user.getId(), initiativeInDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Initiative created successfully"));
    }

    @PostMapping(value = "/{initiativeId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadInitiativeImage(@AuthenticationPrincipal User user,
                                                   @PathVariable Integer initiativeId,
                                                   @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(200).body(initiativeService.uploadInitiativeImage(user.getId(), initiativeId, image));
    }



}
