package com.digitalid.domain.service;

import com.digitalid.domain.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class VerificationService {

    public VerificationResult verifyBasic(Worker worker) {
        boolean valid = worker.isActive();
        return VerificationResult.builder(worker.getWorkerId())
                .valid(valid)
                .status(worker.getStatus())
                .message(valid ? "Worker is active and valid" : "Worker is not active (status: " + worker.getStatus() + ")")
                .build();
    }

    public VerificationResult verifyWithCertHistory(Worker worker, List<Certification> allCerts) {
        boolean valid = worker.isActive();
        return VerificationResult.builder(worker.getWorkerId())
                .valid(valid)
                .status(worker.getStatus())
                .message(valid ? "Verified with " + allCerts.size() + " certification(s) on record"
                        : "Worker is not active")
                .certifications(allCerts)
                .build();
    }

    public VerificationResult verifyWithConditions(Worker worker, List<Certification> allCerts) {
        boolean valid = worker.isActive();

        // filter to background checks and licences
        List<Certification> relevant = allCerts.stream()
                .filter(c -> c.getType().getCategory() == CertificationCategory.BACKGROUND_CHECK
                        || c.getType().getCategory() == CertificationCategory.LICENCE)
                .collect(Collectors.toList());

        // all relevant certs must be valid for conditions to pass
        if (valid && !relevant.isEmpty()) {
            boolean allValid = relevant.stream().allMatch(Certification::isValid);
            if (!allValid) {
                valid = false;
            }
        }

        return VerificationResult.builder(worker.getWorkerId())
                .valid(valid)
                .status(worker.getStatus())
                .message(valid ? "All conditions met" : "One or more conditions not met")
                .certifications(relevant)
                .build();
    }

    public VerificationResult verifyWithPermits(Worker worker, List<Certification> allCerts) {
        List<Certification> permits = allCerts.stream()
                .filter(c -> c.getType().getCategory() == CertificationCategory.PERMIT)
                .collect(Collectors.toList());

        boolean valid = worker.isActive();
        long validPermits = permits.stream().filter(Certification::isValid).count();

        return VerificationResult.builder(worker.getWorkerId())
                .valid(valid && validPermits > 0)
                .status(worker.getStatus())
                .message(validPermits + " valid permit(s) found")
                .certifications(permits)
                .build();
    }

    public VerificationResult verifyWorkAuthorisation(Worker worker, List<WorkAuthorisation> authorisations) {
        boolean valid = worker.isActive();

        if (authorisations.isEmpty()) {
            return VerificationResult.builder(worker.getWorkerId())
                    .valid(false)
                    .status(worker.getStatus())
                    .message("No work authorisation on record")
                    .build();
        }

        // Check if any authorisation is currently valid
        boolean hasValidAuth = authorisations.stream().anyMatch(WorkAuthorisation::isValid);
        if (!hasValidAuth) {
            valid = false;
        }

        return VerificationResult.builder(worker.getWorkerId())
                .valid(valid)
                .status(worker.getStatus())
                .message(valid ? "Work authorisation verified" : "No valid work authorisation found")
                .build();
    }

    public VerificationResult verifyWithAttributes(Worker worker, List<Certification> allCerts) {
        List<Certification> training = allCerts.stream()
                .filter(c -> c.getType().getCategory() == CertificationCategory.TRAINING)
                .collect(Collectors.toList());

        boolean valid = worker.isActive();

        return VerificationResult.builder(worker.getWorkerId())
                .valid(valid)
                .status(worker.getStatus())
                .message(training.size() + " training certification(s) found")
                .certifications(training)
                .build();
    }
}
