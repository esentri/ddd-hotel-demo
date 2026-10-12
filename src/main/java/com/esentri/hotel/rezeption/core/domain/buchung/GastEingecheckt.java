package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.domain.types.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record GastEingecheckt(@NotNull Buchung.BuchungsId buchungsId,
                              @NotNull HotelId hotelId,
                              @NotNull Zimmer.ZimmerId zimmerId) implements DomainEvent {
}
