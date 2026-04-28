package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolTypeTest {

    @Test
    void hasExpectedNumberOfTools() {
        // 2 core + 4 identity + 3 cert + 4 verification + 5 reporting + 3 search + 3 batch + 2 notification = 26
        assertEquals(26, ToolType.values().length);
    }

    @Test
    void coreToolsAreInCoreCategory() {
        assertEquals(ToolCategory.CORE, ToolType.VIEW_WORKER_ID.getCategory());
        assertEquals(ToolCategory.CORE, ToolType.VERIFY_BASIC.getCategory());
    }

    @Test
    void createWorkerIsIdentityManagement() {
        assertEquals(ToolCategory.IDENTITY_MANAGEMENT, ToolType.CREATE_WORKER_ID.getCategory());
        assertEquals("Create Worker ID", ToolType.CREATE_WORKER_ID.getDisplayName());
    }

    @Test
    void verificationToolsAreInCorrectCategory() {
        assertEquals(ToolCategory.ENHANCED_VERIFICATION, ToolType.VERIFY_WITH_CERT_HISTORY.getCategory());
        assertEquals(ToolCategory.ENHANCED_VERIFICATION, ToolType.VERIFY_WITH_CONDITIONS.getCategory());
        assertEquals(ToolCategory.ENHANCED_VERIFICATION, ToolType.VERIFY_WITH_PERMITS.getCategory());
        assertEquals(ToolCategory.ENHANCED_VERIFICATION, ToolType.VERIFY_WITH_ATTRIBUTES.getCategory());
    }

    @Test
    void reportingToolsExist() {
        assertEquals(ToolCategory.REPORTING, ToolType.VIEW_AUDIT_LOG.getCategory());
        assertEquals(ToolCategory.REPORTING, ToolType.CHECK_EXPIRING_CERTS.getCategory());
    }
}
