package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.domain.types.Repository;

import java.util.Optional;

public interface ZimmerRepository extends Repository<Zimmer.ZimmerId, Zimmer> {

    Optional<Zimmer> findByZimmernummer(Zimmernummer zimmernummer);
}
