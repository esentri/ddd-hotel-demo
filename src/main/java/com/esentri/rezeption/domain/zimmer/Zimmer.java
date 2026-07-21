package com.esentri.rezeption.domain.zimmer;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import io.domainlifecycles.domain.types.AggregateRoot;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Zimmer implements AggregateRoot<ZimmerId> {

    private final ZimmerId id;
    private final Zimmerkategorie kategorie;
    private final List<Belegung> belegungen;
    private long concurrencyVersion;

    public Zimmer(ZimmerId id, Zimmerkategorie kategorie) {
        this(id, kategorie, new ArrayList<>(), 0);
    }

    @Builder
    public Zimmer(ZimmerId id, Zimmerkategorie kategorie, List<Belegung> belegungen, long concurrencyVersion) {
        this.id = Objects.requireNonNull(id, "ZimmerId darf nicht null sein");
        this.kategorie = Objects.requireNonNull(kategorie, "Zimmerkategorie darf nicht null sein");
        this.belegungen = belegungen == null ? new ArrayList<>() : new ArrayList<>(belegungen);
        this.concurrencyVersion = concurrencyVersion;
    }

    public boolean istVerfuegbarFuer(Zeitraum zeitraum) {
        Objects.requireNonNull(zeitraum, "Zeitraum darf nicht null sein");
        return belegungen.stream().noneMatch(b -> {
            return b.zeitraum().ueberlapptMit(zeitraum);
        });
    }

    public void belegeFuer(BuchungsId buchungsId, Zeitraum zeitraum) {
        Objects.requireNonNull(buchungsId, "BuchungsId darf nicht null sein");
        Objects.requireNonNull(zeitraum, "Zeitraum darf nicht null sein");

        if (belegungen.stream().anyMatch(b -> b.zeitraum().ueberlapptMit(zeitraum))) {
            throw new IllegalStateException("Zimmer ist im gewaehlten Zeitraum bereits belegt.");
        }
        Belegung neueBelegung = new Belegung(zeitraum, BelegungsTyp.CHECKIN, buchungsId);
        this.belegungen.add(neueBelegung);
    }


    public void planeWartung(Zeitraum zeitraum) {
        Objects.requireNonNull(zeitraum, "Zeitraum darf nicht null sein");

        if (belegungen.stream().anyMatch(b -> b.zeitraum().ueberlapptMit(zeitraum))) {
            throw new IllegalStateException("Zimmer kann nicht fuer Wartung geplant werden, da es bereits belegt ist.");
        }
        Belegung wartung = new Belegung(zeitraum, BelegungsTyp.WARTUNG, null);
        this.belegungen.add(wartung);
    }

    public void gibFrei(BuchungsId buchungsId) {
        Objects.requireNonNull(buchungsId, "BuchungsId darf nicht null sein");
        boolean removed = belegungen.removeIf(b -> b.buchungsId() != null && b.buchungsId().equals(buchungsId));

        if (!removed) {
            throw new IllegalStateException("Keine Belegung fuer diese BuchungsId gefunden.");
        }
    }

    public List<Belegung> getBelegungen() {
        return Collections.unmodifiableList(belegungen);
    }

    @Override
    public ZimmerId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }

    public Zimmerkategorie getKategorie() {
        return kategorie;
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
