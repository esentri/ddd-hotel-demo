package com.esentri.rezeption.domain.zimmer;

import io.domainlifecycles.domain.types.Identity;

import java.util.UUID;

public record ZimmerId(UUID value) implements Identity<UUID> {
    public ZimmerId {
        if (value == null) {
            throw new IllegalArgumentException("ZimmerId value must not be null");
        }
    }
}
