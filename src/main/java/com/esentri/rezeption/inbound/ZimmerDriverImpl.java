package com.esentri.rezeption.inbound;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.reservierung.ReservierungAusgecheckt;
import com.esentri.rezeption.core.domain.zimmer.BeantrageZimmerWartung;
import com.esentri.rezeption.core.domain.zimmer.Belegung;
import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungAbgelehnt;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungBestaetigt;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungEingeplant;
import com.esentri.rezeption.outbound.ZimmerAuslastung;
import com.esentri.rezeption.core.inport.ZimmerDriver;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.ZimmerRepository;
import com.esentri.rezeption.outbound.ZimmerAuslastungProvider;
import lombok.RequiredArgsConstructor;
import nitrox.dlc.domain.types.ListensTo;
import nitrox.dlc.domain.types.Publishes;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Die Implementierung der ZimmerDriver-Schnittstelle, die als Einstiegspunkt zum Managen von Zimmern in einem Hotel dient.
 * Diese Klasse bietet Funktionen für das Beantragen von Zimmerwartungen und das Abrufen verfügbarer Zimmer.
 *
 * @author Mario Herb
 */
@Service
@RequiredArgsConstructor
public class ZimmerDriverImpl implements ZimmerDriver {

    private final ZimmerRepository zimmerRepository;

    private final DomainEventPublisher domainEventPublisher;

    private final ZimmerAuslastungProvider zimmerAuslastungProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    @EventListener
    @ListensTo(domainEventType = ZimmerWartungEingeplant.class)
    public void onEvent(ZimmerWartungEingeplant zimmerWartungEingeplant) {
        zimmerWartungEingeplant.zimmerNummern()
            .forEach(zimmerNummer ->
                handle(
                    new BeantrageZimmerWartung(
                        zimmerWartungEingeplant.planungReferenz(),
                        zimmerWartungEingeplant.von(),
                        zimmerWartungEingeplant.bis(),
                        zimmerNummer
                    )
                )
            );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Zimmer.ZimmerNummer> verfuegbareZimmer(Hotel.HotelId hotelId, LocalDate von, LocalDate bis, ZimmerKategorie zimmerKategorie, int kapazitaet) {
        var probeBelegung = new Belegung(
            von,
            bis,
            BelegungTyp.GAST
        );
        var verfuegbareZimer = zimmerRepository.listZimmerByKategorieAndKapazitaet(
                hotelId,
                zimmerKategorie,
                kapazitaet
            )
            .stream()
            .filter(zimmer -> !zimmer.hatUeberschneidendeBelegung(probeBelegung))
            .map(Zimmer::id)
            .toList();
        return verfuegbareZimer;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Publishes(domainEventTypes = {ZimmerWartungBestaetigt.class, ZimmerWartungAbgelehnt.class})
    public void handle(BeantrageZimmerWartung beantrageZimmerWartung){
        var zimmer = zimmerRepository.findById(beantrageZimmerWartung.zimmerNummer()).orElseThrow();
        if(zimmer.neueBelegung(beantrageZimmerWartung.von(), beantrageZimmerWartung.bis(), BelegungTyp.WARTUNG)){
            zimmerRepository.update(zimmer);
            domainEventPublisher.publish(new ZimmerWartungBestaetigt(beantrageZimmerWartung.wartungsPlanungId(), zimmer.id()));
        }else{
            domainEventPublisher.publish(new ZimmerWartungAbgelehnt(beantrageZimmerWartung.wartungsPlanungId(), zimmer.id()));
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @EventListener
    @ListensTo(domainEventType = ReservierungAusgecheckt.class)
    public void onEvent(ReservierungAusgecheckt reservierungAusgecheckt) {
        var zimmer = zimmerRepository.findById(reservierungAusgecheckt.zimmerNummer()).orElseThrow();
        zimmerRepository.update(zimmer.aktuelleBelegungBeenden());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ZimmerAuslastung> auslastung(Hotel.HotelId hotelId, LocalDate von, LocalDate bis){
        return zimmerAuslastungProvider.auslastung(hotelId, von, bis);
    }
}
