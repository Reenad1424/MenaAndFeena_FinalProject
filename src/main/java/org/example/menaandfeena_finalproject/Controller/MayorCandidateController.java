package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MayorCandidateInDTO;
import org.example.menaandfeena_finalproject.Service.MayorCandidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mayor-candidate")
@RequiredArgsConstructor
public class MayorCandidateController {

    private final MayorCandidateService mayorCandidateService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllMayorCandidates() {
        return ResponseEntity.status(200).body(mayorCandidateService.getAllMayorCandidates());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMayorCandidate(@RequestBody MayorCandidateInDTO mayorCandidateInDTO) {
        mayorCandidateService.addMayorCandidate(mayorCandidateInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Mayor candidate added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMayorCandidate(@PathVariable Integer id, @RequestBody MayorCandidateInDTO mayorCandidateInDTO) {
        mayorCandidateService.updateMayorCandidate(id, mayorCandidateInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Mayor candidate updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMayorCandidate(@PathVariable Integer id) {
        mayorCandidateService.deleteMayorCandidate(id);
        return ResponseEntity.status(200).body(new ApiResponse("Mayor candidate deleted"));
    }
}
