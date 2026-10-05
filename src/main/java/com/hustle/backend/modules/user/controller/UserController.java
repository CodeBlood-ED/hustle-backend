package com.hustle.backend.modules.user.controller;

import com.hustle.backend.common.ApiResponse;
import com.hustle.backend.common.exception.ResourceNotFoundException;
import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.auth.repository.UserRepository;
import com.hustle.backend.modules.user.dto.*;
import com.hustle.backend.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Profile & Enquiries", description = "Endpoints for managing user profile, saved addresses, and service enquiries")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResourceNotFoundException("No authenticated user session found");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userDetails.getUsername()));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get full user profile with summary counts and saved addresses")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        UserProfileDto profile = userService.getUserProfile(user);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile information (name, contact)")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        User user = getAuthenticatedUser(userDetails);
        UserProfileDto updated = userService.updateProfile(user, request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get list of saved delivery addresses")
    public ResponseEntity<ApiResponse<List<AddressDto>>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        List<AddressDto> addresses = userService.getUserAddresses(user);
        return ResponseEntity.ok(ApiResponse.ok(addresses));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Save a new delivery address")
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAddressRequest request) {
        User user = getAuthenticatedUser(userDetails);
        AddressDto address = userService.addAddress(user, request);
        return new ResponseEntity<>(ApiResponse.ok("Address saved successfully", address), HttpStatus.CREATED);
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Delete a saved delivery address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = getAuthenticatedUser(userDetails);
        userService.deleteAddress(user, id);
        return ResponseEntity.ok(ApiResponse.ok("Address removed successfully", null));
    }

    @GetMapping("/enquiries")
    @Operation(summary = "Get user's submitted service requests and enquiries")
    public ResponseEntity<ApiResponse<List<EnquiryDto>>> getEnquiries(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        List<EnquiryDto> enquiries = userService.getUserEnquiries(user);
        return ResponseEntity.ok(ApiResponse.ok(enquiries));
    }

    @PostMapping("/enquiries")
    @Operation(summary = "Submit a new service request or enquiry")
    public ResponseEntity<ApiResponse<EnquiryDto>> createEnquiry(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateEnquiryRequest request) {
        User user = getAuthenticatedUser(userDetails);
        EnquiryDto enquiry = userService.createEnquiry(user, request);
        return new ResponseEntity<>(ApiResponse.ok("Enquiry submitted successfully", enquiry), HttpStatus.CREATED);
    }
}
