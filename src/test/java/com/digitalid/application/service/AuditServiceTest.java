package com.digitalid.application.service;

import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.domain.model.AuditLogEntry;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.OrganisationProfile;
import com.digitalid.domain.model.OrganisationType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    private AuditService auditService;
    private FakeAuditLogRepository fakeRepo;

    @BeforeEach
    void setUp() {
        fakeRepo = new FakeAuditLogRepository();
        auditService = new AuditService(fakeRepo);
    }

    @Test
    void logsActionWithDetails() {
        OrganisationContext context = makeContext();

        auditService.log("CREATE_WORKER", "WK-US-2024-001", "Worker",
                context, "Created new worker");

        assertEquals(1, fakeRepo.entries.size());
        FakeAuditLogRepository.Entry entry = fakeRepo.entries.get(0);
        assertEquals("CREATE_WORKER", entry.action);
        assertEquals("WK-US-2024-001", entry.entityId);
        assertEquals("Worker", entry.entityType);
        assertEquals("ORG-001", entry.organisationId);
        assertEquals("CENTRAL_AUTHORITY", entry.organisationType);
        assertEquals("Created new worker", entry.details);
    }

    @Test
    void logsActionWithoutDetails() {
        OrganisationContext context = makeContext();

        auditService.log("VIEW_WORKER", "WK-UK-2024-003", "Worker", context);

        assertEquals(1, fakeRepo.entries.size());
        assertNull(fakeRepo.entries.get(0).details);
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        return new OrganisationContext("ORG-001", OrganisationType.CENTRAL_AUTHORITY,
                "Central Authority", profile.getAllowedTools());
    }

    // Simple fake implementation for testing
    static class FakeAuditLogRepository implements AuditLogRepository {

        final List<Entry> entries = new ArrayList<>();

        @Override
        public void save(AuditLogEntry auditEntry) {
            entries.add(new Entry(auditEntry.getAction(), auditEntry.getEntityId(),
                    auditEntry.getEntityType(), auditEntry.getOrganisationId(),
                    auditEntry.getOrganisationType(), auditEntry.getDetails()));
        }

        @Override
        public List<AuditLogEntry> findByEntityId(String entityId) {
            return List.of();
        }

        @Override
        public List<AuditLogEntry> findByOrganisationId(String organisationId) {
            return List.of();
        }

        @Override
        public List<AuditLogEntry> findAll() {
            return List.of();
        }

        static class Entry {
            final String action;
            final String entityId;
            final String entityType;
            final String organisationId;
            final String organisationType;
            final String details;

            Entry(String action, String entityId, String entityType,
                  String organisationId, String organisationType, String details) {
                this.action = action;
                this.entityId = entityId;
                this.entityType = entityType;
                this.organisationId = organisationId;
                this.organisationType = organisationType;
                this.details = details;
            }
        }
    }
}
