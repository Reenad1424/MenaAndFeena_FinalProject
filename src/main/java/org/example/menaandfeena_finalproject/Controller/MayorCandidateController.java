package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MayorCandidateInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.CandidateDetailsDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionPageDTO;
import org.example.menaandfeena_finalproject.Service.MayorCandidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/mayor-candidates")
@RequiredArgsConstructor
public class MayorCandidateController {

    private final MayorCandidateService mayorCandidateService;


    // =========================
    // GET ALL CANDIDATES
    // =========================

    @GetMapping
    public ResponseEntity<?> getAllMayorCandidates() {

        return ResponseEntity.status(200).body(
                mayorCandidateService.getAllMayorCandidates()
        );
    }


    // =========================
    // APPLY FOR CANDIDACY
    // =========================

    @PostMapping("/apply/{userId}/round/{roundId}")
    public ResponseEntity<?> applyForMayorCandidacy(
            @PathVariable Integer userId,
            @PathVariable Integer roundId
    ) {

        mayorCandidateService.applyForMayorCandidacy(userId, roundId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم ترشيح المستخدم لمنصب عمدة الحي بنجاح")
        );
    }


    // =========================
    // GET ROUND CANDIDATES
    // =========================

    @GetMapping("/round/{roundId}")
    public ResponseEntity<?> getCandidatesByRound(
            @PathVariable Integer roundId
    ) {

        return ResponseEntity.status(200).body(
                mayorCandidateService.getCandidatesByRound(roundId)
        );
    }


    // =========================
    // GET ELECTION DASHBOARD
    // =========================

    @GetMapping("/round/{roundId}/dashboard")
    public ResponseEntity<?> getElectionDashboard(
            @PathVariable Integer roundId
    ) {

        return ResponseEntity.status(200).body(
                mayorCandidateService.getElectionDashboard(roundId)
        );
    }


    // =========================
    // GET CANDIDATE PROFILE
    // =========================

    @GetMapping("/{candidateId}/profile")
    public ResponseEntity<?> getCandidateProfile(
            @PathVariable Integer candidateId
    ) {

        return ResponseEntity.status(200).body(
                mayorCandidateService.getCandidateProfile(candidateId)
        );
    }


    // =========================
    // UPDATE CANDIDATE
    // =========================

    @PutMapping("/{candidateId}")
    public ResponseEntity<?> updateMayorCandidate(
            @PathVariable Integer candidateId,
            @RequestBody @Valid MayorCandidateInDTO mayorCandidateInDTO
    ) {

        mayorCandidateService.updateMayorCandidate(
                candidateId,
                mayorCandidateInDTO
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث بيانات المرشح بنجاح")
        );
    }


    // =========================
    // DELETE CANDIDATE
    // =========================

    @DeleteMapping("/{candidateId}")
    public ResponseEntity<?> deleteMayorCandidate(
            @PathVariable Integer candidateId
    ) {

        mayorCandidateService.deleteMayorCandidate(candidateId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم حذف المرشح بنجاح")
        );
    }
}
