package com.esentri.rezeption.domain.model.zimmer;

import com.esentri.rezeption.domain.model.buchung.GastAusgecheckt;
import io.domainlifecycles.domain.types.DomainEventListener;
import io.domainlifecycles.domain.types.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZimmerFreigabeService implements DomainService {

    private final ZimmerRepository zimmerRepository;

    @DomainEventListener
    public void freigabeZimmer(GastAusgecheckt event) {
        Zimmer zimmer = zimmerRepository.findById(event.zimmerId())
            .orElseThrow(() -> new IllegalArgumentException("Zimmer nicht gefunden: " + event.zimmerId()));

        zimmer.entferneBelegung(event.buchungId());

        zimmerRepository.update(zimmer);
    }
}
