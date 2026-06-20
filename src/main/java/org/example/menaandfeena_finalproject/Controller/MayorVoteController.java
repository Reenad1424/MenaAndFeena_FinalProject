package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MayorVoteInDTO;
import org.example.menaandfeena_finalproject.Service.MayorVoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mayor-votes")
@RequiredArgsConstructor
public class MayorVoteController {

    private final MayorVoteService mayorVoteService;


    // =========================
    // GET ALL MAYOR VOTES
    // =========================

    @GetMapping
    public ResponseEntity<?> getAllMayorVotes() {

        return ResponseEntity.ok(
                mayorVoteService.getAllMayorVotes()
        );
    }


    // =========================
    // VOTE FOR MAYOR CANDIDATE
    // =========================

    @PostMapping("/vote/user/{userId}/candidate/{candidateId}/round/{roundId}")
    public ResponseEntity<ApiResponse> voteForMayorCandidate(
            @PathVariable Integer userId,
            @PathVariable Integer candidateId,
            @PathVariable Integer roundId
    ) {

        String resultMessage =
                mayorVoteService.voteForMayorCandidate(
                        userId,
                        candidateId,
                        roundId
                );

        return ResponseEntity.ok(
                new ApiResponse(resultMessage)
        );
    }


    // =========================
    // UPDATE MAYOR VOTE
    // =========================

    @PutMapping("/{voteId}")
    public ResponseEntity<ApiResponse> updateMayorVote(
            @PathVariable Integer voteId,
            @RequestBody MayorVoteInDTO mayorVoteInDTO
    ) {

        mayorVoteService.updateMayorVote(voteId, mayorVoteInDTO);

        return ResponseEntity.ok(
                new ApiResponse("تم تحديث التصويت بنجاح")
        );
    }


    // =========================
    // DELETE MAYOR VOTE
    // =========================

    @DeleteMapping("/{voteId}")
    public ResponseEntity<ApiResponse> deleteMayorVote(
            @PathVariable Integer voteId
    ) {

        mayorVoteService.deleteMayorVote(voteId);

        return ResponseEntity.ok(
                new ApiResponse("تم حذف التصويت بنجاح")
        );
    }
}