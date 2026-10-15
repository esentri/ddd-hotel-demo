package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.ZimmerService;
import com.esentri.hotel.rezeption.core.domain.zimmer.LegeZimmerAn;
import com.esentri.hotel.rezeption.core.domain.zimmer.VerfuegbaresZimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rezeption/zimmer")
class ZimmerController {

    record ZimmerAngelegtResponse(UUID zimmerId) {
    }

    private final ZimmerService zimmerService;

    ZimmerController(ZimmerService zimmerService) {
        this.zimmerService = zimmerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ZimmerAngelegtResponse legeZimmerAn(@RequestBody LegeZimmerAn command) {
        return new ZimmerAngelegtResponse(zimmerService.legeZimmerAn(command).value());
    }

    /**
     * Uebersicht fuer die Zimmerauswahl mit dem Gast beim Check-in.
     */
    @GetMapping("/verfuegbar")
    List<VerfuegbaresZimmer> findeVerfuegbareZimmer(@RequestParam Zimmerkategorie gewuenschteZimmerkategorie) {
        return zimmerService.findeVerfuegbareZimmer(gewuenschteZimmerkategorie);
    }
}
