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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    // USER CRUD - ADMIN
    // =========================

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers() {

        return ResponseEntity.status(200).body(
                userService.getAllUsers()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(
            @RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto
    ) {

        userService.createUser(userRegisterRequestDto);

        return ResponseEntity.status(201).body(
                new ApiResponse("User added successfully")
        );
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto
    ) {

        userService.updateUser(
                userId,
                userRegisterRequestDto
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("User updated successfully")
        );
    }

    @DeleteMapping("/delete/{userId}")
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

        return ResponseEntity.status(200).body(
                userService.getWelcomeScreen()
        );
    }

    @GetMapping("/about")
    public ResponseEntity<?> getAboutInfo() {

        return ResponseEntity.status(200).body(
                userService.getAboutInfo()
        );
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
    // NEIGHBORHOOD RESIDENTS
    // =========================

    @GetMapping("/neighborhood-residents")
    public ResponseEntity<?> getNeighborhoodResidents(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getNeighborhoodResidents(user.getId())
        );
    }


    // =========================
    // USER ACTIVITY LOG
    // =========================

    @GetMapping("/activity-log")
    public ResponseEntity<?> getUserActivityLog(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getUserActivityLog(user.getId())
        );
    }


    // =========================
    // USER PROFILES
    // =========================

    @GetMapping("/profile/full")
    public ResponseEntity<?> getFullProfile(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getUserProfileDetails(user.getId())
        );
    }

    @GetMapping("/profile/basic")
    public ResponseEntity<?> getBasicProfile(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getBasicProfile(user.getId())
        );
    }

    @GetMapping("/profile/community")
    public ResponseEntity<?> getCommunityProfile(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getCommunityProfile(user.getId())
        );
    }

    @GetMapping("/profile/activities")
    public ResponseEntity<?> getActivitiesProfile(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getActivitiesProfile(user.getId())
        );
    }

    @GetMapping("/profile/reputation")
    public ResponseEntity<?> getReputationProfile(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getReputationProfile(user.getId())
        );
    }

    @GetMapping("/profile/marketplace")
    public ResponseEntity<?> getMarketplaceProfile(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.status(200).body(
                userService.getMarketplaceProfile(user.getId())
        );
    }
}