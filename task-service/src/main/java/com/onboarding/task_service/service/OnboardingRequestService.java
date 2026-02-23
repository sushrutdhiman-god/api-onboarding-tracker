package com.onboarding.task_service.service;

import com.onboarding.task_service.entity.OnboardingRequest;
import com.onboarding.task_service.enums.OnboardingStatus;
import com.onboarding.task_service.repository.OnboardingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingRequestService {

    private final OnboardingRequestRepository repository;

    // REQUESTER — submit new onboarding request
    public OnboardingRequest submitRequest(OnboardingRequest request, String requesterId) {
        request.setRequesterId(requesterId);
        request.setStatus(OnboardingStatus.SUBMITTED);
        return repository.save(request);
    }

    // SYSTEM — auto allocate to least occupied developer
    public OnboardingRequest allocateDeveloper(String requestId, List<String> developerIds) {
        OnboardingRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        String leastOccupiedDev = developerIds.stream()
                .min((dev1, dev2) -> {
                    long dev1Count = repository.countByAssignedDeveloperIdAndStatusNot(
                            dev1, OnboardingStatus.COMPLETED);
                    long dev2Count = repository.countByAssignedDeveloperIdAndStatusNot(
                            dev2, OnboardingStatus.COMPLETED);
                    return Long.compare(dev1Count, dev2Count);
                })
                .orElseThrow(() -> new RuntimeException("No developers available"));

        request.setAssignedDeveloperId(leastOccupiedDev);
        request.setStatus(OnboardingStatus.ALLOCATED);
        return repository.save(request);
    }

    // LEAD — approve request
    public OnboardingRequest approveRequest(String requestId) {
        OnboardingRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        request.setStatus(OnboardingStatus.LEAD_APPROVED);
        return repository.save(request);
    }

    // LEAD — reassign developer
    public OnboardingRequest reassignDeveloper(String requestId, String newDeveloperId) {
        OnboardingRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        request.setAssignedDeveloperId(newDeveloperId);
        return repository.save(request);
    }

    // DEV — update status and progress notes
    public OnboardingRequest updateProgress(String requestId,
                                            OnboardingStatus newStatus,
                                            String progressNotes) {
        OnboardingRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        request.setStatus(newStatus);
        if (progressNotes != null) {
            request.setProgressNotes(progressNotes);
        }
        return repository.save(request);
    }

    // REQUESTER — get own requests
    public List<OnboardingRequest> getRequestsByRequester(String requesterId) {
        return repository.findByRequesterId(requesterId);
    }

    // DEV — get allocated requests
    public List<OnboardingRequest> getRequestsByDeveloper(String developerId) {
        return repository.findByAssignedDeveloperId(developerId);
    }

    // LEAD — get all requests
    public List<OnboardingRequest> getAllRequests() {
        return repository.findAll();
    }

    // ALL — get single request by ID
    public OnboardingRequest getRequestById(String requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
    }
}