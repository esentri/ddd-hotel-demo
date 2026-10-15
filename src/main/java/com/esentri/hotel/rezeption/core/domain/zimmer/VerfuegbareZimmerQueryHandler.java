package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.domain.types.QueryHandler;

import java.util.List;

/**
 * Uebersicht der verfuegbaren (bezugsfertigen) Zimmer der gewuenschten und aller hoeheren Zimmerkategorien,
 * gebuchte Kategorie zuerst.
 */
public interface VerfuegbareZimmerQueryHandler extends QueryHandler<VerfuegbaresZimmer> {

    List<VerfuegbaresZimmer> findeVerfuegbareZimmer(Zimmerkategorie gewuenschteZimmerkategorie);
}
