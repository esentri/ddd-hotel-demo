package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record Email(@NotBlank @jakarta.validation.constraints.Email @Size(max = 200) String value)
    implements ValueObject {
}
