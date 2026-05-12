package com.esentri.rezeption.domain.model.rechnung;

import io.domainlifecycles.domain.types.Identity;

import java.util.UUID;

public record RechnungId(UUID value) implements Identity<UUID> {
    public RechnungId {
        if (value == null) {
            throw new IllegalArgumentException("RechnungId value cannot be null");
        }
    }
}
