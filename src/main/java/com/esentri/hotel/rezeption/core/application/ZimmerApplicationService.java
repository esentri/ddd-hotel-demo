package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.buchung.GastAusgecheckt;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.FreieZimmerQueryHandler;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.FreieZimmerUebersicht;
import com.esentri.hotel.rezeption.core.domain.zimmer.BeendeWartung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegung;
import com.esentri.hotel.rezeption.core.domain.zimmer.LegeZimmerAn;
import com.esentri.hotel.rezeption.core.domain.zimmer.PlaneWartung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.domain.types.ApplicationService;
import io.domainlifecycles.domain.types.DomainEventListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Pflege der Zimmer eines Hotels. Vorerst manuell; spaeter ggf. aus einer Hotelverwaltung synchronisiert.
 */
public class ZimmerApplicationService implements ApplicationService {

    /** Zeitraum der Uebersicht freier Zimmer fuer die UI. */
    static final int TAGE_IN_UEBERSICHT = 14;

    private final ZimmerRepository zimmerRepository;
    private final FreieZimmerQueryHandler freieZimmerQueryHandler;
    private final Clock clock;

    public ZimmerApplicationService(ZimmerRepository zimmerRepository,
                                    FreieZimmerQueryHandler freieZimmerQueryHandler,
                                    Clock clock) {
        this.zimmerRepository = zimmerRepository;
        this.freieZimmerQueryHandler = freieZimmerQueryHandler;
        this.clock = clock;
    }

    @Transactional
    public Zimmer.ZimmerId legeZimmerAn(LegeZimmerAn legeZimmerAn) {
        if (zimmerRepository.findByHotelUndZimmernummer(legeZimmerAn.hotelId(), legeZimmerAn.zimmernummer())
            .isPresent()) {
            throw new ZimmerBereitsVorhandenException(legeZimmerAn.zimmernummer());
        }
        return zimmerRepository.insert(Zimmer.legeAn(legeZimmerAn.hotelId(), legeZimmerAn.zimmernummer(),
            legeZimmerAn.kategorie())).id();
    }

    @Transactional
    public Belegung.BelegungId planeWartung(PlaneWartung planeWartung) {
        var zimmer = zimmerImHotel(planeWartung.hotelId(), planeWartung.zimmernummer());
        var belegungId = zimmer.planeWartung(planeWartung.wartungsgrund(), planeWartung.zeitraum());
        zimmerRepository.update(zimmer);
        return belegungId;
    }

    @Transactional
    public void beendeWartung(BeendeWartung beendeWartung) {
        var zimmer = zimmerImHotel(beendeWartung.hotelId(), beendeWartung.zimmernummer());
        zimmerRepository.update(zimmer.beendeWartung(beendeWartung.belegungId(), LocalDate.now(clock)));
    }

    /**
     * Gibt das Zimmer nach dem Checkout frei. Wird nach dem Commit des Checkouts in einer eigenen Transaktion
     * aufgerufen; schlaegt es fehl, bleibt der Checkout bestehen und das Zimmer belegt.
     */
    @DomainEventListener
    public void onGastAusgecheckt(GastAusgecheckt gastAusgecheckt) {
        var zimmer = zimmerRepository.findById(gastAusgecheckt.zimmerId())
            .orElseThrow(() -> new NichtGefundenException(
                "Das Zimmer %s existiert nicht.".formatted(gastAusgecheckt.zimmerId().value())));
        zimmerRepository.update(
            zimmer.beendeGastBelegung(gastAusgecheckt.buchungsId(), gastAusgecheckt.ausgechecktAm()));
    }

    /**
     * Freie Zimmer des Hotels fuer die naechsten zwei Wochen ab heute.
     */
    @Transactional(readOnly = true)
    public FreieZimmerUebersicht freieZimmerDerNaechstenZweiWochen(HotelId hotelId) {
        var heute = LocalDate.now(clock);
        return freieZimmerQueryHandler.freieZimmer(hotelId, heute, heute.plusDays(TAGE_IN_UEBERSICHT));
    }

    private Zimmer zimmerImHotel(HotelId hotelId, Zimmernummer zimmernummer) {
        return zimmerRepository.findByHotelUndZimmernummer(hotelId, zimmernummer)
            .orElseThrow(() -> new NichtGefundenException(
                "Das Hotel hat kein Zimmer mit der Nummer %s.".formatted(zimmernummer.value())));
    }
}
