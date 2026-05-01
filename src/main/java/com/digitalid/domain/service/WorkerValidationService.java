package com.digitalid.domain.service;

import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;

import java.time.LocalDate;

public class WorkerValidationService {

    public void validateCreation(String fullName, LocalDate dateOfBirth, String email) {
        if (fullName == null || fullName.isBlank()) {
            throw new ValidationException("Worker name cannot be empty");
        }
        if (dateOfBirth == null || !dateOfBirth.isBefore(LocalDate.now())) {
            throw new ValidationException("Date of birth must be in the past");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new ValidationException("Email must be a valid format");
        }
    }

    public void validateStatusChange(Worker worker, WorkerStatus newStatus) {
        if (!worker.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidOperationException(
                    "Cannot change status from " + worker.getStatus() + " to " + newStatus);
        }
    }

    public void validateUpdate(Worker worker) {
        if (!worker.canBeUpdated()) {
            throw new InvalidOperationException(
                    "Cannot update worker " + worker.getWorkerId() + ". Status is " + worker.getStatus());
        }
    }
}
