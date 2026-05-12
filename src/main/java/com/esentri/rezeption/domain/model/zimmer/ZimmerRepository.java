package com.esentri.rezeption.domain.model.zimmer;

import io.domainlifecycles.domain.types.Repository;

import java.util.Optional;

public interface ZimmerRepository extends Repository<ZimmerId, Zimmer> {

    Optional<Zimmer> findById(ZimmerId id);

    Zimmer update(Zimmer zimmer);

    Zimmer insert(Zimmer zimmer);
}
