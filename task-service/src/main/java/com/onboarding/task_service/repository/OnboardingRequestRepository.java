package com.onboarding.task_service.repository;

import com.onboarding.task_service.entity.OnboardingRequest;
import com.onboarding.task_service.enums.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingRequestRepository
        extends JpaRepository<OnboardingRequest, String> {

    List<OnboardingRequest> findByRequesterId(String requesterId);

    List<OnboardingRequest> findByAssignedDeveloperId(String developerId);

    List<OnboardingRequest> findByStatus(OnboardingStatus status);

    long countByAssignedDeveloperIdAndStatusNot(
            String developerId, OnboardingStatus status);
}

//This one is for our allocation engine — it counts how many active SRTs a developer has (excluding COMPLETED ones). We'll use this to find the least occupied developer.//