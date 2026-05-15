package com.esentri.rezeption.application.zimmer;

import com.esentri.rezeption.domain.auslastung.Zimmerauslastung;
import com.esentri.rezeption.domain.auslastung.ZimmerauslastungQueryHandler;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.domain.types.ApplicationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Application Service fuer Zimmer-bezogene Anwendungsfaelle.
 */
public class ZimmerApplicationService implements ApplicationService {

    private final ZimmerauslastungQueryHandler zimmerauslastungQueryHandler;

    public ZimmerApplicationService(ZimmerauslastungQueryHandler zimmerauslastungQueryHandler) {
        this.zimmerauslastungQueryHandler = Objects.requireNonNull(zimmerauslastungQueryHandler, "ZimmerauslastungQueryHandler darf nicht null sein");
    }

    /**
     * Ermittelt die Zimmerauslastung fuer einen bestimmten Zeitraum und optional eine Kategorie.
     *
     * @param von             Beginn des Zeitraums (inklusiv)
     * @param bis             Ende des Zeitraums (inklusiv)
     * @param zimmerkategorie Zimmerkategorie als Filter (optional)
     * @return Liste der Zimmerauslastungen
     */
    public List<Zimmerauslastung> findeZimmerauslastung(LocalDate von, LocalDate bis, Zimmerkategorie zimmerkategorie) {
        return zimmerauslastungQueryHandler.findeZimmerauslastung(von, bis, zimmerkategorie);
    }
}
