package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.ContactRequestDto;
import org.example.menaandfeena_finalproject.DTO.In.UserRegisterRequestDto;
import org.example.menaandfeena_finalproject.DTO.Out.*;
import org.example.menaandfeena_finalproject.Model.User;
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
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(
            @RequestBody @Valid User user
    ) {
        userService.createUser(user);
        return ResponseEntity.status(201).body(
                new ApiResponse("User added successfully")
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Integer userId,
            @RequestBody @Valid User user
    ) {
        userService.updateUser(userId, user);
        return ResponseEntity.ok(
                new ApiResponse("User updated successfully")
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable Integer userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(
                new ApiResponse("User deleted successfully")
        );
    }


    // =========================
    // PUBLIC INFO
    // =========================

    @GetMapping("/welcome")
    public ResponseEntity<WelcomeDTO> getWelcomeScreen() {
        return ResponseEntity.ok(userService.getWelcomeScreen());
    }

    @GetMapping("/about")
    public ResponseEntity<AboutInfoDTO> getAboutInfo() {
        return ResponseEntity.ok(userService.getAboutInfo());
    }

    @PostMapping("/contact")
    public ResponseEntity<ApiResponse> sendContactMessage(
            @RequestBody @Valid ContactRequestDto dto
    ) {
        userService.sendContactMessage(dto);

        return ResponseEntity.ok(
                new ApiResponse("تم إرسال رسالتك بنجاح، وسيتواصل معك فريق الدعم قريباً")
        );
    }


    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @RequestBody @Valid UserRegisterRequestDto dto
    ) {
        UserRegisterResponseDto response =
                userService.registerUser(dto);

        String message =
                "هلا والله "
                        + response.getFullName()
                        + " في حي "
                        + response.getDetectedNeighborhoodName();

        return ResponseEntity.status(201).body(
                new ApiResponse(message)
        );
    }


    // =========================
    // NEIGHBORHOOD RESIDENTS
    // =========================

    @GetMapping("/{userId}/neighborhood-residents")
    public ResponseEntity<List<NeighborhoodResidentDTO>> getNeighborhoodResidents(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getNeighborhoodResidents(userId)
        );
    }


    // =========================
    // USER ACTIVITY LOG
    // =========================

    @GetMapping("/{userId}/activity-log")
    public ResponseEntity<UserActivityLogDTO> getUserActivityLog(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getUserActivityLog(userId)
        );
    }


    // =========================
    // USER PROFILES
    // =========================

    @GetMapping("/{userId}/profile/full")
    public ResponseEntity<UserProfileDetailsDTO> getFullProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getUserProfileDetails(userId)
        );
    }

    @GetMapping("/{userId}/profile/basic")
    public ResponseEntity<UserBasicInfoDTO> getBasicProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getBasicProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/community")
    public ResponseEntity<UserProfileCommunityDTO> getCommunityProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getCommunityProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/activities")
    public ResponseEntity<UserProfileActivitiesDTO> getActivitiesProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getActivitiesProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/reputation")
    public ResponseEntity<UserProfileReputationDTO> getReputationProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getReputationProfile(userId)
        );
    }

    @GetMapping("/{userId}/profile/marketplace")
    public ResponseEntity<UserProfileMarketplaceDTO> getMarketplaceProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(
                userService.getMarketplaceProfile(userId)
        );
    }
}