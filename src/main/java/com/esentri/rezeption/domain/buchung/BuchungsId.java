package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Identity;

import java.util.UUID;

public record BuchungsId(UUID value) implements Identity<UUID> {
    public BuchungsId {
        if (value == null) {
            throw new IllegalArgumentException("BuchungsId value must not be null");
        }
    }
}
