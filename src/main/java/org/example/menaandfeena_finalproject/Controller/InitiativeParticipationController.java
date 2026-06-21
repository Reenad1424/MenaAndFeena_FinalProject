package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.InitiativeParticipationInDTO;
import org.example.menaandfeena_finalproject.Service.InitiativeParticipationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/initiative-participation")
@RequiredArgsConstructor
public class InitiativeParticipationController {
    private final InitiativeParticipationService initiativeParticipationService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllInitiativeParticipations() {
        return ResponseEntity.status(200).body(initiativeParticipationService.getAllInitiativeParticipations());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addInitiativeParticipation(
            @Valid @RequestBody InitiativeParticipationInDTO initiativeParticipationInDTO) {

        initiativeParticipationService.addInitiativeParticipation(initiativeParticipationInDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Initiative participation added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateInitiativeParticipation(
            @PathVariable Integer id,
            @Valid @RequestBody InitiativeParticipationInDTO initiativeParticipationInDTO) {

        initiativeParticipationService.updateInitiativeParticipation(id, initiativeParticipationInDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Initiative participation updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInitiativeParticipation(@PathVariable Integer id) {

        initiativeParticipationService.deleteInitiativeParticipation(id);

        return ResponseEntity.status(200).body(new ApiResponse("Initiative participation deleted successfully"));
    }


    @PostMapping("/join/{userId}/{initiativeId}")
    public ResponseEntity<?> joinInitiative(@PathVariable Integer userId, @PathVariable Integer initiativeId) {
        initiativeParticipationService.joinInitiative(userId, initiativeId);
        return ResponseEntity.status(200).body(new ApiResponse("Joined successfully"));
    }



    @PostMapping("/join-family/{familyMemberId}/{initiativeId}")
    public ResponseEntity<?> joinFamilyMember(@PathVariable Integer familyMemberId, @PathVariable Integer initiativeId) {
        initiativeParticipationService.joinFamilyMember(familyMemberId, initiativeId);
        return ResponseEntity.status(200).body(new ApiResponse("Family member joined successfully"));
    }



    @GetMapping("/{initiativeId}/participants")
    public ResponseEntity<?> getParticipants(@PathVariable Integer initiativeId) {
        return ResponseEntity.status(200).body(initiativeParticipationService.getParticipants(initiativeId));
    }





}
