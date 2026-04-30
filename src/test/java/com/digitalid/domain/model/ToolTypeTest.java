package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolTypeTest {

    @Test
    void coreToolsIncludeWorkAuthorisationVerification() {
        assertEquals(ToolCategory.CORE, ToolType.VIEW_WORKER_ID.getCategory());
        assertEquals(ToolCategory.CORE, ToolType.VERIFY_BASIC.getCategory());
        assertEquals(ToolCategory.CORE, ToolType.VERIFY_WORK_AUTHORISATION.getCategory());
    }

    @Test
    void createWorkerIsIdentityManagement() {
        assertEquals(ToolCategory.IDENTITY_MANAGEMENT, ToolType.CREATE_WORKER_ID.getCategory());
    }

    @Test
    void regionalComplianceIsReporting() {
        assertEquals(ToolCategory.REPORTING, ToolType.CHECK_REGIONAL_COMPLIANCE.getCategory());
    }

    @Test
    void searchIsNowASingleTool() {
        assertEquals(ToolCategory.SEARCH, ToolType.SEARCH_WORKERS.getCategory());
    }
}
