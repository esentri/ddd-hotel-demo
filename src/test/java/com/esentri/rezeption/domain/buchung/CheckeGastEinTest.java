package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.zimmer.ZimmerId;
import io.domainlifecycles.validation.extend.ValidationDomainClassExtender;
import io.domainlifecycles.validation.jakarta.JakartaBeanValidationDomainAssertionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckeGastEinTest {

    @BeforeAll
    public static void beforeAll(){
        ValidationDomainClassExtender.extend("com.esentri.rezeption");
    }

    @Test
    void testValidCommand() {
        assertDoesNotThrow(() -> new CheckeGastEin(
            new BuchungsId(UUID.randomUUID()),
            new ZimmerId(UUID.randomUUID())
        ));
    }

    @Test
    void testMissingBuchungsId() {
        assertThrows(JakartaBeanValidationDomainAssertionException.class, () -> new CheckeGastEin(
            null,
            new ZimmerId(UUID.randomUUID())
        ));
    }

    @Test
    void testMissingZimmerId() {
        assertThrows(JakartaBeanValidationDomainAssertionException.class, () -> new CheckeGastEin(
            new BuchungsId(UUID.randomUUID()),
            null
        ));
    }
}
