package com.digitalid.domain.model;

import com.digitalid.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogEntryTest {

    @Test
    void createsEntryWithAllFields() {
        LocalDateTime now = LocalDateTime.of(2024, 6, 15, 10, 30);
        AuditLogEntry entry = new AuditLogEntry("AUD-001", "CREATE_WORKER",
                "WK-US-2024-001", "Worker", "ORG-001", "CENTRAL_AUTHORITY",
                "Created new worker", now);

        assertEquals("AUD-001", entry.getId());
        assertEquals("CREATE_WORKER", entry.getAction());
        assertEquals("WK-US-2024-001", entry.getEntityId());
        assertEquals("Worker", entry.getEntityType());
        assertEquals("ORG-001", entry.getOrganisationId());
        assertEquals("CENTRAL_AUTHORITY", entry.getOrganisationType());
        assertEquals("Created new worker", entry.getDetails());
        assertEquals(now, entry.getTimestamp());
    }

    @Test
    void defaultsTimestampToNowWhenNull() {
        AuditLogEntry entry = new AuditLogEntry("AUD-002", "UPDATE_STATUS",
                "WK-UK-2024-005", "Worker", "ORG-002", "FINE_DINING",
                "Status changed to SUSPENDED", null);

        assertNotNull(entry.getTimestamp());
    }

    @Test
    void rejectsBlankAction() {
        assertThrows(ValidationException.class, () ->
                new AuditLogEntry("AUD-003", "", "WK-US-2024-001", "Worker",
                        "ORG-001", "CENTRAL_AUTHORITY", "details", null));
    }

    @Test
    void rejectsNullAction() {
        assertThrows(ValidationException.class, () ->
                new AuditLogEntry("AUD-004", null, "WK-US-2024-001", "Worker",
                        "ORG-001", "CENTRAL_AUTHORITY", "details", null));
    }

    @Test
    void rejectsBlankEntityId() {
        assertThrows(ValidationException.class, () ->
                new AuditLogEntry("AUD-005", "CREATE_WORKER", "", "Worker",
                        "ORG-001", "CENTRAL_AUTHORITY", "details", null));
    }
}
