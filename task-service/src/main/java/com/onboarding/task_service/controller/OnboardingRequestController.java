package com.onboarding.task_service.controller;

import com.onboarding.task_service.entity.OnboardingRequest;
import com.onboarding.task_service.enums.OnboardingStatus;
import com.onboarding.task_service.service.OnboardingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingRequestController {

    private final OnboardingRequestService service;

    // REQUESTER — submit new request
    @PostMapping("/requests")
    public ResponseEntity<OnboardingRequest> submitRequest(
            @RequestBody OnboardingRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String requesterId = jwt.getSubject();
        return ResponseEntity.ok(service.submitRequest(request, requesterId));
    }

    // REQUESTER — get my requests
    @GetMapping("/requests/my")
    public ResponseEntity<List<OnboardingRequest>> getMyRequests(
            @AuthenticationPrincipal Jwt jwt) {
        String requesterId = jwt.getSubject();
        return ResponseEntity.ok(service.getRequestsByRequester(requesterId));
    }

    // DEV — get my allocated requests
    @GetMapping("/requests/allocated")
    public ResponseEntity<List<OnboardingRequest>> getAllocatedRequests(
            @AuthenticationPrincipal Jwt jwt) {
        String developerId = jwt.getSubject();
        return ResponseEntity.ok(service.getRequestsByDeveloper(developerId));
    }

    // DEV — update progress
    @PatchMapping("/requests/{id}/progress")
    public ResponseEntity<OnboardingRequest> updateProgress(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        OnboardingStatus status = OnboardingStatus.valueOf(body.get("status"));
        String notes = body.get("progressNotes");
        return ResponseEntity.ok(service.updateProgress(id, status, notes));
    }

    // LEAD — get all requests
    @GetMapping("/requests")
    public ResponseEntity<List<OnboardingRequest>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    // LEAD — approve request
    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<OnboardingRequest> approveRequest(
            @PathVariable String id) {
        return ResponseEntity.ok(service.approveRequest(id));
    }

    // LEAD — allocate developer
    @PostMapping("/requests/{id}/allocate")
    public ResponseEntity<OnboardingRequest> allocateDeveloper(
            @PathVariable String id,
            @RequestBody Map<String, List<String>> body) {
        List<String> developerIds = body.get("developerIds");
        return ResponseEntity.ok(service.allocateDeveloper(id, developerIds));
    }

    // LEAD — reassign developer
    @PatchMapping("/requests/{id}/reassign")
    public ResponseEntity<OnboardingRequest> reassignDeveloper(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String newDeveloperId = body.get("developerId");
        return ResponseEntity.ok(service.reassignDeveloper(id, newDeveloperId));
    }

    // ALL — get single request
    @GetMapping("/requests/{id}")
    public ResponseEntity<OnboardingRequest> getRequest(
            @PathVariable String id) {
        return ResponseEntity.ok(service.getRequestById(id));
    }
// TEMP //
    @GetMapping("/debug/roles")
    public ResponseEntity<?> debugRoles(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
                "subject", jwt.getSubject(),
                "claims", jwt.getClaims(),
                "realm_access", jwt.getClaimAsMap("realm_access")
        ));
    }
}