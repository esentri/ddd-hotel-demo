package com.esentri.rezeption.domain.zimmer;

import io.domainlifecycles.domain.types.Identity;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ZimmerId(@NotNull(message = "ZimmerId darf nicht null sein") UUID value)
        implements Identity<UUID> {}
