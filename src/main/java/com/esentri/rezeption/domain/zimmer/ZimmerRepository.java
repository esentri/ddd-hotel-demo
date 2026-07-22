package com.esentri.rezeption.domain.zimmer;

import io.domainlifecycles.domain.types.Repository;

import java.util.List;
import java.util.Optional;

public interface ZimmerRepository extends Repository<ZimmerId, Zimmer> {
    Optional<Zimmer> findById(ZimmerId id);
    List<Zimmer> findByKategorie(Zimmerkategorie kategorie);
    Zimmer insert(Zimmer zimmer);
    Zimmer update(Zimmer zimmer);
}
