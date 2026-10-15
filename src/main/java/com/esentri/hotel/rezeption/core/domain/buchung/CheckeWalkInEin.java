package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Ein Gast ohne Buchung kommt an den Tresen und wird sofort eingecheckt, das mit ihm ausgewaehlte Zimmer wird
 * zugewiesen. Die Anreise ist der heutige Tag.
 */
@Builder
public record CheckeWalkInEin(
    @Size(max = 100) String vorname,
    @NotBlank @Size(max = 100) String nachname,
    Adresse adresse,
    LocalDate geburtsdatum,
    @NotNull LocalDate abreise,
    @NotNull Zimmerkategorie gewuenschteZimmerkategorie,
    @NotNull Zimmer.ZimmerId zimmerId
) implements DomainCommand {
}
