package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record Wartungsgrund(@NotBlank @Size(max = 200) String value) implements ValueObject {
}
