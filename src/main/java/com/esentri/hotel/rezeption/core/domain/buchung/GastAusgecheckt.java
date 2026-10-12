package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.domain.types.DomainEvent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GastAusgecheckt(@NotNull Buchung.BuchungsId buchungsId,
                              @NotNull HotelId hotelId,
                              @NotNull Zimmer.ZimmerId zimmerId,
                              @NotNull LocalDate ausgechecktAm) implements DomainEvent {
}
