package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.FamilyMemberInDTO;
import org.example.menaandfeena_finalproject.Service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/family-member")
@RequiredArgsConstructor
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    @GetMapping("/my-family/{userId}")
    public ResponseEntity<?> getMyFamily(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(familyMemberService.getMyFamily(userId));
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> add(@PathVariable Integer userId, @RequestBody @Valid FamilyMemberInDTO familyMemberInDTO) {
        familyMemberService.add(userId, familyMemberInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Family member added successfully"));
    }


    @PutMapping("/update/{userId}/{id}")
    public ResponseEntity<?> update(@PathVariable Integer userId, @PathVariable Integer id, @RequestBody @Valid FamilyMemberInDTO familyMemberInDTO) {
        familyMemberService.update(userId, id, familyMemberInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Family member updated successfully"));
    }

    @DeleteMapping("/delete/{userId}/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer userId, @PathVariable Integer id) {
        familyMemberService.delete(userId, id);
        return ResponseEntity.status(200).body(new ApiResponse("Family member deleted successfully"));
    }
}
