package com.esentri.rezeption.domain.zimmer;

import com.esentri.rezeption.domain.ZimmerId;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.Zeitraum;
import io.domainlifecycles.domain.types.AggregateRoot;

import java.util.Objects;

public class Zimmer implements AggregateRoot<ZimmerId> {

    private final ZimmerId id;
    private ZimmerStatus status;

    public Zimmer(ZimmerId id, ZimmerStatus status) {
        this.id = Objects.requireNonNull(id, "ZimmerId darf nicht null sein");
        this.status = Objects.requireNonNull(status, "Status darf nicht null sein");
    }

    public boolean istVerfuegbarFuer(Zeitraum zeitraum) {
        Objects.requireNonNull(zeitraum, "Zeitraum darf nicht null sein");
        return this.status != ZimmerStatus.WARTUNG && this.status == ZimmerStatus.FREI;
    }

    public void belegeFuer(BuchungsId buchungsId, Zeitraum zeitraum) {
        Objects.requireNonNull(buchungsId, "BuchungsId darf nicht null sein");
        Objects.requireNonNull(zeitraum, "Zeitraum darf nicht null sein");
        
        if (!istVerfuegbarFuer(zeitraum)) {
            throw new IllegalStateException("Zimmer ist nicht verfuegbar");
        }
        
        this.status = ZimmerStatus.BELEGT;
    }

    public void gibFrei(BuchungsId buchungsId) {
        Objects.requireNonNull(buchungsId, "BuchungsId darf nicht null sein");
        // In einem echten System wuerden wir hier pruefen, ob das Zimmer
        // tatsaechlich fuer diese BuchungsId belegt ist.
        // Fuer dieses Demo-Projekt setzen wir einfach den Status auf FREI.
        if (this.status != ZimmerStatus.BELEGT) {
            throw new IllegalStateException("Zimmer ist nicht belegt und kann daher nicht freigegeben werden");
        }
        this.status = ZimmerStatus.FREI;
    }

    @Override
    public ZimmerId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return 0;
    }

    public ZimmerStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zimmer zimmer = (Zimmer) o;
        return Objects.equals(id, zimmer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
