package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.domain.types.Repository;

import java.util.List;
import java.util.Optional;

public interface BuchungRepository extends Repository<Buchung.BuchungsId, Buchung> {

    Optional<Buchung> findByReservierungsnummer(Reservierungsnummer reservierungsnummer);

    List<Buchung> findEingecheckteBuchungen(Zimmer.ZimmerId zimmerId);
}
