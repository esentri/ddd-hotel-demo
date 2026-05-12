package com.esentri.rezeption.domain.model.rechnung;

import io.domainlifecycles.domain.types.Repository;

import java.util.Optional;

public interface RechnungRepository extends Repository<RechnungId, Rechnung> {
    Optional<Rechnung> findById(RechnungId id);
    Rechnung insert(Rechnung rechnung);
}
