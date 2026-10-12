package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record Telefonnummer(@NotBlank @Pattern(regexp = "\\+?[0-9 ()/-]{4,30}") String value) implements ValueObject {
}
