package org.example.menaandfeena_finalproject.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Service.InitiativeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/initiative")
@RequiredArgsConstructor
public class InitiativeController {

    private final InitiativeService initiativeService;

    @GetMapping("/get")
    public ResponseEntity getAllInitiatives() {
        return ResponseEntity.status(200).body(initiativeService.getAllInitiatives());
    }

    @PostMapping("/add")
    public ResponseEntity addInitiative(@Valid @RequestBody Initiative initiative) {
        initiativeService.addInitiative(initiative);
        return ResponseEntity.status(200).body(new ApiResponse("Initiative added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateInitiative(@PathVariable Integer id,
                                           @Valid @RequestBody Initiative initiative) {
        initiativeService.updateInitiative(id, initiative);
        return ResponseEntity.status(200).body(new ApiResponse("Initiative updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteInitiative(@PathVariable Integer id) {
        initiativeService.deleteInitiative(id);
        return ResponseEntity.status(200).body(new ApiResponse("Initiative deleted successfully"));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity getInitiativesByCategory(@PathVariable String category) {
        return ResponseEntity.status(200).body(initiativeService.getInitiativesByCategory(category));
    }


    @GetMapping("/upcoming")
    public ResponseEntity getUpcomingInitiatives() {
        return ResponseEntity.status(200).body(initiativeService.getUpcomingInitiatives());
    }


    @GetMapping("/{id}")
    public ResponseEntity getInitiativeById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(initiativeService.getInitiativeById(id));

    }



    @PostMapping("/create/{userId}")
    public ResponseEntity createInitiative(@PathVariable Integer userId, @Valid @RequestBody Initiative initiative) {

        initiativeService.createInitiative(userId, initiative);

        return ResponseEntity.status(200).body(new ApiResponse("Initiative created successfully"));
    }







}
