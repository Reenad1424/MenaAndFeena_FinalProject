package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/family-member")
@RequiredArgsConstructor
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    @GetMapping("/get-all")
    public ResponseEntity<List<FamilyMember>> getAll() {
        return ResponseEntity.status(200).body(familyMemberService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody @Valid FamilyMember familyMember) {
        familyMemberService.add(familyMember);
        return ResponseEntity.status(201).body(new ApiResponse("Family member added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody @Valid FamilyMember familyMember) {
        familyMemberService.update(id, familyMember);
        return ResponseEntity.status(200).body(new ApiResponse("Family member updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        familyMemberService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Family member deleted successfully"));
    }
}
