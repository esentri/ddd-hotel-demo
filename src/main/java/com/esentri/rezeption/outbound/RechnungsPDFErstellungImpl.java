package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.outport.RechnungsPDFErstellung;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Dummy-Service, der die PDF-Erzeugung für eine Rechnung simuliert.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
@Service
public class RechnungsPDFErstellungImpl implements RechnungsPDFErstellung {

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] erzeugePDF(Rechnung.Id rechnungsId) {
        return new byte[0];
    }
}
