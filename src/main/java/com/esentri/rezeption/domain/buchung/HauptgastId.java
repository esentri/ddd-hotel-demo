package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Identity;
import lombok.Builder;

import java.util.UUID;

@Builder
public record HauptgastId(UUID id) implements Identity<UUID> {
    public HauptgastId {
        if (id == null) {
            throw new IllegalArgumentException("HauptgastId darf nicht null sein.");
        }
    }

    @Override
    public UUID value() {
        return id;
    }
}
