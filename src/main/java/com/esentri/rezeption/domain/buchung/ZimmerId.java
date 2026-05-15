package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Identity;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ZimmerId(UUID id) implements Identity<UUID> {
    public ZimmerId {
        if (id == null) {
            throw new IllegalArgumentException("ZimmerId darf nicht null sein.");
        }
    }

    @Override
    public UUID value() {
        return id;
    }
}
