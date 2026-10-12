package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.domain.zimmer.Belegungszeitraum;
import com.esentri.hotel.rezeption.core.domain.zimmer.Wartungsgrund;

public record WartungsAnfrage(Wartungsgrund wartungsgrund, Belegungszeitraum zeitraum) {
}
