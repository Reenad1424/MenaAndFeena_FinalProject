package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.ContactRequestDto;
import org.example.menaandfeena_finalproject.DTO.In.UserRegisterRequestDto;
import org.example.menaandfeena_finalproject.DTO.Out.*;
import org.example.menaandfeena_finalproject.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // =========================
    // USER CRUD
    // =========================

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto
    ) {
        userService.createUser(userRegisterRequestDto);
        return ResponseEntity.status(200).body(
                new ApiResponse("User added successfully")
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto
    ) {
        userService.updateUser(userId, userRegisterRequestDto);
        return ResponseEntity.status(200).body(
                new ApiResponse("User updated successfully")
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Integer userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.status(200).body(
                new ApiResponse("User deleted successfully")
        );
    }


    // =========================
    // PUBLIC INFO
    // =========================

    @GetMapping("/welcome")
    public ResponseEntity<?> getWelcomeScreen() {
        return ResponseEntity.status(200).body(userService.getWelcomeScreen());
    }

    @GetMapping("/about")
    public ResponseEntity<?> getAboutInfo() {
        return ResponseEntity.status(200).body(userService.getAboutInfo());
    }

    @PostMapping("/contact")
    public ResponseEntity<?> sendContactMessage(
            @RequestBody @Valid ContactRequestDto dto
    ) {
        userService.sendContactMessage(dto);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم إرسال رسالتك بنجاح، وسيتواصل معك فريق الدعم قريباً")
        );
    }


    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid UserRegisterRequestDto dto
    ) {
        UserRegisterResponseDto response =
                userService.registerUser(dto);

        String message =
                "هلا والله "
                        + response.getFullName()
                        + " في حي "
                        + response.getDetectedNeighborhoodName();

        return ResponseEntity.status(200).body(
                new ApiResponse(message)
        );
    }


    // =========================
    // NEIGHBORHOOD RESIDENTS
    // =========================

    @GetMapping("/{userId}/neighborhood-residents")
    public ResponseEntity<?> getNeighborhoodResidents(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getNeighborhoodResidents(userId)
        );
    }


    // =========================
    // USER ACTIVITY LOG
    // =========================

    @GetMapping("/{userId}/activity-log")
    public ResponseEntity<?> getUserActivityLog(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getUserActivityLog(userId)
        );
    }


    // =========================
    // USER PROFILES
    // =========================

    @GetMapping("/{userId}/profile/full")
    public ResponseEntity<?> getFullProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getUserProfileDetails(userId)
        );
    }

    @GetMapping("/{userId}/profile/basic")
    public ResponseEntity<?> getBasicProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getBasicProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/community")
    public ResponseEntity<?> getCommunityProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getCommunityProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/activities")
    public ResponseEntity<?> getActivitiesProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getActivitiesProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/reputation")
    public ResponseEntity<?> getReputationProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getReputationProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/marketplace")
    public ResponseEntity<?> getMarketplaceProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.status(200).body(
                userService.getMarketplaceProfile(userId)
        );
    }
}
