package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Eine Reservierung aus dem externen Reservierungssystem wird als Buchung in die Rezeption uebernommen.
 */
@Builder
public record UebernimmReservierung(@NotNull HotelId hotelId,
                                    @NotNull Reservierungsnummer reservierungsnummer,
                                    @NotNull Name name,
                                    Email email,
                                    Telefonnummer telefonnummer,
                                    Adresse adresse,
                                    @NotNull Aufenthaltszeitraum aufenthaltszeitraum,
                                    @NotNull Zimmerkategorie zimmerkategorie) implements DomainCommand {
}
