package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Service.MayorVoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mayor-votes")
@RequiredArgsConstructor
public class MayorVoteController {

    private final MayorVoteService mayorVoteService;

    @GetMapping
    public ResponseEntity<?> getAllMayorVotes() {
        return ResponseEntity.status(200).body(mayorVoteService.getAllMayorVotes());
    }

    @PostMapping("/vote/user/{userId}/candidate/{candidateId}/round/{roundId}")
    public ResponseEntity<?> voteForMayorCandidate(
            @PathVariable Integer userId,
            @PathVariable Integer candidateId,
            @PathVariable Integer roundId
    ) {
        String resultMessage = mayorVoteService.voteForMayorCandidate(userId, candidateId, roundId);
        return ResponseEntity.status(200).body(new ApiResponse(resultMessage));
    }

    @DeleteMapping("/{voteId}")
    public ResponseEntity<?> deleteMayorVote(@PathVariable Integer voteId) {
        mayorVoteService.deleteMayorVote(voteId);
        return ResponseEntity.status(200).body(new ApiResponse("Mayor vote deleted successfully"));
    }
}
