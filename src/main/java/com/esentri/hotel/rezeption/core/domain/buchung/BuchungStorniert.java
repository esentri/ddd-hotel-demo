package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import io.domainlifecycles.domain.types.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record BuchungStorniert(@NotNull Buchung.BuchungsId buchungsId, @NotNull HotelId hotelId)
    implements DomainEvent {
}
