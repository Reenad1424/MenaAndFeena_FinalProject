package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.ElectionRoundInDTO;
import org.example.menaandfeena_finalproject.Service.ElectionRoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/election-round")
@RequiredArgsConstructor
public class ElectionRoundController {

    private final ElectionRoundService electionRoundService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllElectionRounds() {
        return ResponseEntity.status(200).body(electionRoundService.getAllElectionRounds());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addElectionRound(@RequestBody ElectionRoundInDTO electionRoundInDTO) {
        electionRoundService.addElectionRound(electionRoundInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Election round added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateElectionRound(@PathVariable Integer id, @RequestBody ElectionRoundInDTO electionRoundInDTO) {
        electionRoundService.updateElectionRound(id, electionRoundInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Election round updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteElectionRound(@PathVariable Integer id) {
        electionRoundService.deleteElectionRound(id);
        return ResponseEntity.status(200).body(new ApiResponse("Election round deleted"));
    }
}
