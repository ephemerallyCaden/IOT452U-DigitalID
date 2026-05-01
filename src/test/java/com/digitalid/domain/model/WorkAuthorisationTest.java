package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkAuthorisationTest {

    private WorkAuthorisation makeAuth(Region region, LocalDate verified, LocalDate expiry) {
        return new WorkAuthorisation("WA-001", "WK-GB-2024-001", region,
                verified, List.of("Passport", "BRP"), expiry, "Central Authority");
    }

    @Test
    void indefiniteRightToWorkNeverExpires() {
        // UK citizen has no expiry
        WorkAuthorisation auth = makeAuth(Region.UNITED_KINGDOM, LocalDate.of(2024, 3, 1), null);
        assertFalse(auth.isExpired());
        assertFalse(auth.needsReverification());
        assertTrue(auth.isValid());
    }

    @Test
    void expiredVisaMeansInvalidAuthorisation() {
        WorkAuthorisation auth = makeAuth(Region.UNITED_KINGDOM,
                LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));
        assertTrue(auth.isExpired());
        assertFalse(auth.isValid());
    }

    @Test
    void futureExpiryIsStillValid() {
        WorkAuthorisation auth = makeAuth(Region.UNITED_KINGDOM,
                LocalDate.of(2024, 1, 1), LocalDate.now().plusDays(180));
        assertFalse(auth.isExpired());
        assertTrue(auth.isValid());
    }

    @Test
    void ukRequiresVerificationBeforeHireDate() {
        LocalDate hireDate = LocalDate.of(2024, 5, 1);

        // verified day before (passes)
        WorkAuthorisation before = makeAuth(Region.UNITED_KINGDOM, LocalDate.of(2024, 4, 30), null);
        assertTrue(before.meetsRegionalTimingRequirement(hireDate));

        // verified day after (fails)
        WorkAuthorisation after = makeAuth(Region.UNITED_KINGDOM, LocalDate.of(2024, 5, 2), null);
        assertFalse(after.meetsRegionalTimingRequirement(hireDate));
    }

    @Test
    void usAllowsThreeBusinessDaysAfterHire() {
        LocalDate hireDate = LocalDate.of(2024, 4, 1); // Monday

        // verified Wednesday (2 business days)
        WorkAuthorisation ok = makeAuth(Region.UNITED_STATES, LocalDate.of(2024, 4, 3), null);
        assertTrue(ok.meetsRegionalTimingRequirement(hireDate));

        // verified next Monday (5 business days)
        WorkAuthorisation late = makeAuth(Region.UNITED_STATES, LocalDate.of(2024, 4, 8), null);
        assertFalse(late.meetsRegionalTimingRequirement(hireDate));
    }

    @Test
    void usThreeBusinessDaysSkipsWeekend() {
        // hired Thursday. Deadline is next Tuesday
        LocalDate hireDate = LocalDate.of(2024, 4, 4); // Thursday
        WorkAuthorisation auth = makeAuth(Region.UNITED_STATES, LocalDate.of(2024, 4, 9), null);
        assertTrue(auth.meetsRegionalTimingRequirement(hireDate));
    }

    @Test
    void documentsDefensivelyCopied() {
        List<String> docs = new ArrayList<>(List.of("Passport"));
        WorkAuthorisation auth = new WorkAuthorisation("WA-002", "WK-GB-2024-002",
                Region.UNITED_KINGDOM, LocalDate.now(), docs, null, "Central Authority");
        docs.add("Tampered");
        assertEquals(1, auth.getDocumentsPresented().size());
    }
}
