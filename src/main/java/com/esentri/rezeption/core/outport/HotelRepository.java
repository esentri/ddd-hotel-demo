package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import nitrox.dlc.domain.types.Repository;

/**
 * Repository für Hotels.
 *
 * @author Mario Herb
 */
public interface HotelRepository extends Repository<Hotel.HotelId, Hotel> {
}