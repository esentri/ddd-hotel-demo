package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record Name(@NotBlank @Size(max = 100) String vorname,
                   @NotBlank @Size(max = 100) String nachname) implements ValueObject {
}
