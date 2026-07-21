package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Identity;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BuchungsId(@NotNull(message = "BuchungsId darf nicht null sein") UUID value)
        implements Identity<UUID> {}
