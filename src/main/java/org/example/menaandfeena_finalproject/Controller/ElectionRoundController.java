package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.ElectionRoundInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionRoundDetailsDTO;
import org.example.menaandfeena_finalproject.Service.ElectionRoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/election-rounds")
@RequiredArgsConstructor
public class ElectionRoundController {

    private final ElectionRoundService electionRoundService;


    // =========================
    // GET ALL ELECTION ROUNDS
    // =========================

    @GetMapping
    public ResponseEntity<?> getAllElectionRounds() {

        return ResponseEntity.status(200).body(
                electionRoundService.getAllElectionRounds()
        );
    }


    // =========================
    // GET ELECTION ROUND DETAILS
    // also closes round and selects winner if expired
    // =========================

    @GetMapping("/{roundId}/details")
    public ResponseEntity<?> getElectionRoundDetails(
            @PathVariable Integer roundId
    ) {

        return ResponseEntity.status(200).body(
                electionRoundService.getElectionRoundDetails(roundId)
        );
    }


    // =========================
    // CREATE ELECTION ROUND
    // =========================

    @PostMapping
    public ResponseEntity<?> createElectionRound(
            @RequestBody @Valid ElectionRoundInDTO electionRoundInDTO
    ) {

        electionRoundService.createElectionRound(electionRoundInDTO);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم إنشاء جولة انتخابية جديدة بنجاح")
        );
    }


    // =========================
    // UPDATE ELECTION ROUND
    // =========================

    @PutMapping("/{roundId}")
    public ResponseEntity<?> updateElectionRound(
            @PathVariable Integer roundId,
            @RequestBody @Valid ElectionRoundInDTO electionRoundInDTO
    ) {

        electionRoundService.updateElectionRound(
                roundId,
                electionRoundInDTO
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث الجولة الانتخابية بنجاح")
        );
    }


    // =========================
    // DELETE ELECTION ROUND
    // =========================

    @DeleteMapping("/{roundId}")
    public ResponseEntity<?> deleteElectionRound(
            @PathVariable Integer roundId
    ) {

        electionRoundService.deleteElectionRound(roundId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم حذف الجولة الانتخابية بنجاح")
        );
    }
}