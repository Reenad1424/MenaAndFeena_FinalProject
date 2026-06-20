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

        return ResponseEntity.ok(
                mayorCandidateService.getAllMayorCandidates()
        );
    }


    // =========================
    // APPLY FOR CANDIDACY
    // =========================

    @PostMapping("/apply/{userId}/round/{roundId}")
    public ResponseEntity<ApiResponse> applyForMayorCandidacy(
            @PathVariable Integer userId,
            @PathVariable Integer roundId
    ) {

        mayorCandidateService.applyForMayorCandidacy(userId, roundId);

        return ResponseEntity.status(201).body(
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

        return ResponseEntity.ok(
                mayorCandidateService.getCandidatesByRound(roundId)
        );
    }


    // =========================
    // GET ELECTION DASHBOARD
    // =========================

    @GetMapping("/round/{roundId}/dashboard")
    public ResponseEntity<ElectionPageDTO> getElectionDashboard(
            @PathVariable Integer roundId
    ) {

        return ResponseEntity.ok(
                mayorCandidateService.getElectionDashboard(roundId)
        );
    }


    // =========================
    // GET CANDIDATE PROFILE
    // =========================

    @GetMapping("/{candidateId}/profile")
    public ResponseEntity<CandidateDetailsDTO> getCandidateProfile(
            @PathVariable Integer candidateId
    ) {

        return ResponseEntity.ok(
                mayorCandidateService.getCandidateProfile(candidateId)
        );
    }


    // =========================
    // UPDATE CANDIDATE
    // =========================

    @PutMapping("/{candidateId}")
    public ResponseEntity<ApiResponse> updateMayorCandidate(
            @PathVariable Integer candidateId,
            @RequestBody @Valid MayorCandidateInDTO mayorCandidateInDTO
    ) {

        mayorCandidateService.updateMayorCandidate(
                candidateId,
                mayorCandidateInDTO
        );

        return ResponseEntity.ok(
                new ApiResponse("تم تحديث بيانات المرشح بنجاح")
        );
    }


    // =========================
    // DELETE CANDIDATE
    // =========================

    @DeleteMapping("/{candidateId}")
    public ResponseEntity<ApiResponse> deleteMayorCandidate(
            @PathVariable Integer candidateId
    ) {

        mayorCandidateService.deleteMayorCandidate(candidateId);

        return ResponseEntity.ok(
                new ApiResponse("تم حذف المرشح بنجاح")
        );
    }
}
