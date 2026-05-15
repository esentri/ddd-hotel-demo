package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.ZimmerId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckeGastEinTest {

    @Test
    void testValidCommand() {
        assertDoesNotThrow(() -> new CheckeGastEin(
            new BuchungsId(UUID.randomUUID()),
            new ZimmerId(UUID.randomUUID())
        ));
    }

    @Test
    void testMissingBuchungsId() {
        assertThrows(NullPointerException.class, () -> new CheckeGastEin(
            null,
            new ZimmerId(UUID.randomUUID())
        ));
    }

    @Test
    void testMissingZimmerId() {
        assertThrows(NullPointerException.class, () -> new CheckeGastEin(
            new BuchungsId(UUID.randomUUID()),
            null
        ));
    }
}
