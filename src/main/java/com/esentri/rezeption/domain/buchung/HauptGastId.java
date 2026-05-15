package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Identity;

import java.util.UUID;

public record HauptGastId(UUID value) implements Identity<UUID> {
    public HauptGastId {
        if (value == null) {
            throw new IllegalArgumentException("HauptGastId value must not be null");
        }
    }
}
