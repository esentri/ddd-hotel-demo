package com.esentri.hotel.rezeption.core.domain.verfuegbarkeit;

import io.domainlifecycles.domain.types.ReadModel;

import java.time.LocalDate;
import java.util.List;

/**
 * Verfuegbarkeit fuer die Nacht ab {@code datum}, je Zimmerkategorie.
 */
public record FreieZimmerAmTag(LocalDate datum, List<VerfuegbarkeitJeKategorie> kategorien) implements ReadModel {
}
