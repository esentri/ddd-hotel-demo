package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Repository;

import java.util.Optional;

public interface BuchungRepository extends Repository<BuchungsId, Buchung> {

    Optional<Buchung> findById(BuchungsId id);

    Buchung insert(Buchung buchung);

    Buchung update(Buchung buchung);

    void delete(BuchungsId id);
}
