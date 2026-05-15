package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Identity;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BuchungsId(UUID id) implements Identity<UUID> {
    public BuchungsId {
        if (id == null) {
            throw new IllegalArgumentException("BuchungsId darf nicht null sein.");
        }
    }

    @Override
    public UUID value() {
        return id;
    }
}
