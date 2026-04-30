package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificationResultTest {

    @Test
    void basicVerificationResult() {
        VerificationResult result = VerificationResult.builder("WK-GB-2024-001")
                .valid(true)
                .status(WorkerStatus.ACTIVE)
                .message("Worker is active and verified")
                .build();

        assertTrue(result.isValid());
        assertEquals("WK-GB-2024-001", result.getWorkerId());
        assertEquals(WorkerStatus.ACTIVE, result.getStatus());
    }

    @Test
    void invalidVerification() {
        VerificationResult result = VerificationResult.builder("WK-GB-2024-010")
                .valid(false)
                .status(WorkerStatus.REVOKED)
                .message("Worker has been revoked")
                .build();

        assertFalse(result.isValid());
    }
}
