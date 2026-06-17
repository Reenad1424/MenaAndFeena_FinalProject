package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.AIModeration;
import org.example.menaandfeena_finalproject.Service.AIModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai-moderation")
@RequiredArgsConstructor
public class AIModerationController {

    private final AIModerationService aiModerationService;

    @GetMapping("/get-all")
    public ResponseEntity<List<AIModeration>> getAll() {
        return ResponseEntity.status(200).body(aiModerationService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody @Valid AIModeration aiModeration) {
        aiModerationService.add(aiModeration);
        return ResponseEntity.status(201).body(new ApiResponse("AI Moderation entry created successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody @Valid AIModeration aiModeration) {
        aiModerationService.update(id, aiModeration);
        return ResponseEntity.status(200).body(new ApiResponse("AI Moderation entry updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        aiModerationService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("AI Moderation entry deleted successfully"));
    }
}
