package com.esentri.rezeption.domain.model.zimmer.auslastung;

import com.esentri.rezeption.domain.model.zimmer.Zimmerkategorie;
import io.domainlifecycles.domain.types.ReadModel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * ReadModel für die Zimmerauslastung pro Kategorie und Tag.
 */
public record Zimmerauslastung(
    @NotNull LocalDate datum,
    @NotNull Zimmerkategorie kategorie,
    @PositiveOrZero int belegteZimmer,
    @PositiveOrZero int zimmerGesamt
) implements ReadModel {
}
