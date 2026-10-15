package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.buchung.BuchungAusgecheckt;
import com.esentri.hotel.rezeption.core.domain.zimmer.LegeZimmerAn;
import com.esentri.hotel.rezeption.core.domain.zimmer.VerfuegbareZimmerQueryHandler;
import com.esentri.hotel.rezeption.core.domain.zimmer.VerfuegbaresZimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerFactory;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.domain.types.ApplicationService;
import io.domainlifecycles.domain.types.DomainEventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ZimmerService implements ApplicationService {

    private final ZimmerFactory zimmerFactory;
    private final ZimmerRepository zimmerRepository;
    private final VerfuegbareZimmerQueryHandler verfuegbareZimmerQueryHandler;

    public ZimmerService(ZimmerFactory zimmerFactory,
                         ZimmerRepository zimmerRepository,
                         VerfuegbareZimmerQueryHandler verfuegbareZimmerQueryHandler) {
        this.zimmerFactory = zimmerFactory;
        this.zimmerRepository = zimmerRepository;
        this.verfuegbareZimmerQueryHandler = verfuegbareZimmerQueryHandler;
    }

    @Transactional
    public Zimmer.ZimmerId legeZimmerAn(LegeZimmerAn command) {
        return zimmerRepository.insert(zimmerFactory.legeAn(command)).getId();
    }

    /**
     * Nach dem Check-out wird das zugewiesene Zimmer geraeumt und ist danach zu reinigen.
     */
    @DomainEventListener
    public void onBuchungAusgecheckt(BuchungAusgecheckt event) {
        var zimmer = zimmerRepository.findById(event.zugewiesenesZimmer()).orElseThrow();
        zimmer.raeume(event.buchungId());
        zimmerRepository.update(zimmer);
    }

    @Transactional(readOnly = true)
    public List<VerfuegbaresZimmer> findeVerfuegbareZimmer(Zimmerkategorie gewuenschteZimmerkategorie) {
        return verfuegbareZimmerQueryHandler.findeVerfuegbareZimmer(gewuenschteZimmerkategorie);
    }
}
