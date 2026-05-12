package com.esentri.rezeption.domain.model.zimmer;

import io.domainlifecycles.domain.types.Identity;

public record ZimmerId(Long value) implements Identity<Long> {
}
