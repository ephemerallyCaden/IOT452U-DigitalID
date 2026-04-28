package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkerStatusTest {

    @Test
    void activeCanTransitionToSuspendedOrRevoked() {
        assertTrue(WorkerStatus.ACTIVE.canTransitionTo(WorkerStatus.SUSPENDED));
        assertTrue(WorkerStatus.ACTIVE.canTransitionTo(WorkerStatus.REVOKED));
    }

    @Test
    void suspendedCanBeReactivatedOrRevoked() {
        assertTrue(WorkerStatus.SUSPENDED.canTransitionTo(WorkerStatus.ACTIVE));
        assertTrue(WorkerStatus.SUSPENDED.canTransitionTo(WorkerStatus.REVOKED));
    }

    @Test
    void revokedIsTerminal() {
        assertFalse(WorkerStatus.REVOKED.canTransitionTo(WorkerStatus.ACTIVE));
        assertFalse(WorkerStatus.REVOKED.canTransitionTo(WorkerStatus.SUSPENDED));
    }

    @Test
    void cannotTransitionToSameStatus() {
        assertFalse(WorkerStatus.ACTIVE.canTransitionTo(WorkerStatus.ACTIVE));
        assertFalse(WorkerStatus.SUSPENDED.canTransitionTo(WorkerStatus.SUSPENDED));
    }

    @Test
    void displayNames() {
        assertEquals("Active", WorkerStatus.ACTIVE.getDisplayName());
        assertEquals("Revoked", WorkerStatus.REVOKED.getDisplayName());
    }
}
