package com.digitalid.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    void workerNotFoundFormatsIdIntoMessage() {
        WorkerNotFoundException ex = new WorkerNotFoundException("WK-US-2024-001");
        assertTrue(ex.getMessage().contains("WK-US-2024-001"));
    }

    @Test
    void unauthorisedAccessShowsOrgAndToolInMessage() {
        UnauthorisedAccessException ex = new UnauthorisedAccessException("Mega Slice Pizza", "CREATE_WORKER");
        assertTrue(ex.getMessage().contains("Mega Slice Pizza"));
        assertTrue(ex.getMessage().contains("CREATE_WORKER"));
    }
}
