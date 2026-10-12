package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record StorniereBuchung(@NotNull Buchung.BuchungsId buchungsId) implements DomainCommand {
}
