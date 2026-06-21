package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.ElectionRoundInDTO;
import org.example.menaandfeena_finalproject.Service.ElectionRoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/election-rounds")
@RequiredArgsConstructor
public class ElectionRoundController {

    private final ElectionRoundService electionRoundService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllElectionRounds() {
        return ResponseEntity.status(200).body(
                electionRoundService.getAllElectionRounds()
        );
    }

    @GetMapping("/get/{roundId}/details")
    public ResponseEntity<?> getElectionRoundDetails(
            @PathVariable Integer roundId
    ) {
        return ResponseEntity.status(200).body(
                electionRoundService.getElectionRoundDetails(roundId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> createElectionRound(
            @RequestBody @Valid ElectionRoundInDTO electionRoundInDTO
    ) {
        electionRoundService.createElectionRound(electionRoundInDTO);

        return ResponseEntity.status(201).body(
                new ApiResponse("تم إنشاء جولة انتخابية جديدة بنجاح")
        );
    }

    @PutMapping("/update/{roundId}")
    public ResponseEntity<?> updateElectionRound(
            @PathVariable Integer roundId,
            @RequestBody @Valid ElectionRoundInDTO electionRoundInDTO
    ) {
        electionRoundService.updateElectionRound(roundId, electionRoundInDTO);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث الجولة الانتخابية بنجاح")
        );
    }

    @DeleteMapping("/delete/{roundId}")
    public ResponseEntity<?> deleteElectionRound(
            @PathVariable Integer roundId
    ) {
        electionRoundService.deleteElectionRound(roundId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم حذف الجولة الانتخابية بنجاح")
        );
    }
}