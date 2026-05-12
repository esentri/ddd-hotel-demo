package com.esentri.rezeption.domain.model.buchung;

import io.domainlifecycles.domain.types.Identity;

public record BuchungId(Long value) implements Identity<Long> {
}
