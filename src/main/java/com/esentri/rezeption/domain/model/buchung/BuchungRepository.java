package com.esentri.rezeption.domain.model.buchung;

import io.domainlifecycles.domain.types.Repository;

import java.util.Optional;

public interface BuchungRepository extends Repository<BuchungId, Buchung> {
    
    Optional<Buchung> findById(BuchungId id);
    
    Buchung update(Buchung buchung);
    
    Buchung insert(Buchung buchung);
}
