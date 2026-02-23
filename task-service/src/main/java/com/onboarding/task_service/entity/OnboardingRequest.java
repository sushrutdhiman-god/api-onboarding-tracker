package com.onboarding.task_service.entity;

import com.onboarding.task_service.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "onboarding_requests")
public class OnboardingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String srtNumber;

    @Column(nullable = false)
    private String releaseMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "request_gateways", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "gateway")
    private List<Gateway> gateways;

    @Enumerated(EnumType.STRING)
    private Flavour flavour;

    private String upstreamSystem;
    private String downstreamSystem;
    private String endpointUrl;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingStatus status;

    private String assignedDeveloperId;
    private String requesterId;

    @Column(length = 2000)
    private String progressNotes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = OnboardingStatus.SUBMITTED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}