package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.BuchungService;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/rezeption/buchungen")
class BuchungController {

    private final BuchungService buchungService;

    BuchungController(BuchungService buchungService) {
        this.buchungService = buchungService;
    }

    @PostMapping("/{buchungId}/checkout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkeAus(@PathVariable UUID buchungId) {
        buchungService.checkeAus(new CheckeAus(new Buchung.BuchungId(buchungId)));
    }
}
