package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import io.domainlifecycles.domain.types.Repository;

import java.util.Optional;

public interface ZimmerRepository extends Repository<Zimmer.ZimmerId, Zimmer> {

    Optional<Zimmer> findByHotelUndZimmernummer(HotelId hotelId, Zimmernummer zimmernummer);
}
