package com.digitalid.domain.model;

import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Worker {

    private final String workerId;
    private final String fullName;
    private final LocalDate dateOfBirth;
    private String email;
    private Region region;
    private WorkerStatus status;
    private final List<Certification> certifications;
    private WorkAuthorisation workAuthorisation;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Worker(String workerId, String fullName, LocalDate dateOfBirth,
                  String email, Region region) {
        if (fullName == null || fullName.isBlank()) {
            throw new ValidationException("Worker name cannot be empty");
        }
        if (dateOfBirth == null || !dateOfBirth.isBefore(LocalDate.now())) {
            throw new ValidationException("Date of birth must be in the past");
        }
        if (!isValidEmail(email)) {
            throw new ValidationException("Email must be a valid format");
        }

        this.workerId = workerId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.region = region;
        this.status = WorkerStatus.ACTIVE;
        this.certifications = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == WorkerStatus.ACTIVE;
    }

    public boolean canBeUpdated() {
        return status != WorkerStatus.REVOKED;
    }

    public void changeStatus(WorkerStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidOperationException(
                    "Cannot transition from " + status + " to " + newStatus);
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateEmail(String newEmail) {
        if (!canBeUpdated()) {
            throw new InvalidOperationException("Cannot update a revoked worker");
        }
        if (!isValidEmail(newEmail)) {
            throw new ValidationException("Email must be a valid format");
        }
        this.email = newEmail;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRegion(Region newRegion) {
        if (!canBeUpdated()) {
            throw new InvalidOperationException("Cannot update a revoked worker");
        }
        this.region = newRegion;
        this.updatedAt = LocalDateTime.now();
    }

    public void addCertification(Certification cert) {
        this.certifications.add(cert);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasCertificationForRegion(Region targetRegion) {
        return certifications.stream()
                .anyMatch(c -> c.getType().getHomeRegion() == targetRegion && c.isValid());
    }

    public List<Certification> getValidCertifications() {
        return certifications.stream()
                .filter(Certification::isValid)
                .collect(Collectors.toList());
    }

    public List<Certification> getCertificationsForRegion(Region targetRegion) {
        return certifications.stream()
                .filter(c -> c.getType().getHomeRegion() == targetRegion)
                .collect(Collectors.toList());
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // Getters

    public String getWorkerId() { return workerId; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getEmail() { return email; }
    public Region getRegion() { return region; }
    public WorkerStatus getStatus() { return status; }
    public List<Certification> getCertifications() { return List.copyOf(certifications); }
    public WorkAuthorisation getWorkAuthorisation() { return workAuthorisation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void assignWorkAuthorisation(WorkAuthorisation workAuthorisation) {
        if (!canBeUpdated()) {
            throw new InvalidOperationException("Cannot assign work authorisation to a revoked worker");
        }
        if (workAuthorisation == null) {
            throw new ValidationException("Work authorisation cannot be null");
        }
        this.workAuthorisation = workAuthorisation;
        this.updatedAt = LocalDateTime.now();
    }
}
