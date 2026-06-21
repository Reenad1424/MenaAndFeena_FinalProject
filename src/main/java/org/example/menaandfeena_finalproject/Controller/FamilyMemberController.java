package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.FamilyMemberInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/family-members")
@RequiredArgsConstructor
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    @GetMapping("/get")
    public ResponseEntity<?> getMyFamily(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(200).body(
                familyMemberService.getMyFamily(user.getId())
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFamilyMember(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid FamilyMemberInDTO familyMemberInDTO
    ) {
        familyMemberService.add(user.getId(), familyMemberInDTO);

        return ResponseEntity.status(201).body(
                new ApiResponse("Family member added successfully")
        );
    }

    @PutMapping("/update/{familyMemberId}")
    public ResponseEntity<?> updateFamilyMember(
            @AuthenticationPrincipal User user,
            @PathVariable Integer familyMemberId,
            @RequestBody @Valid FamilyMemberInDTO familyMemberInDTO
    ) {
        familyMemberService.update(
                user.getId(),
                familyMemberId,
                familyMemberInDTO
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Family member updated successfully")
        );
    }

    @DeleteMapping("/delete/{familyMemberId}")
    public ResponseEntity<?> deleteFamilyMember(
            @AuthenticationPrincipal User user,
            @PathVariable Integer familyMemberId
    ) {
        familyMemberService.delete(
                user.getId(),
                familyMemberId
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Family member deleted successfully")
        );
    }
}