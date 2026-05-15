package com.esentri.rezeption.domain.model.zimmer;

import com.esentri.rezeption.domain.model.Zeitraum;
import com.esentri.rezeption.domain.model.buchung.BuchungId;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.AggregateRoot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Zimmer implements AggregateRoot<ZimmerId> {

    @EqualsAndHashCode.Include
    @NotNull
    private ZimmerId id;

    @NotBlank
    private String nummer;

    @NotNull
    private Zimmerkategorie kategorie;

    @NotNull
    private List<Belegung> belegungen = new ArrayList<>();

    public Zimmer(ZimmerId id, String nummer, Zimmerkategorie kategorie) {
        this.id = id;
        this.nummer = nummer;
        this.kategorie = kategorie;
    }

    public void fügeBelegungHinzu(Zeitraum zeitraum, BuchungId buchungId) {
        boolean überschneidung = belegungen.stream()
            .anyMatch(b -> b.zeitraum().überschneidet(zeitraum));

        DomainAssertions.isFalse(überschneidung, "Das Zimmer ist im gewählten Zeitraum bereits belegt.");

        this.belegungen.add(new Belegung(zeitraum, buchungId));
    }

    public void entferneBelegung(BuchungId buchungId) {
        this.belegungen.removeIf(b -> b.buchungId().equals(buchungId));
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
        return 0;
    }
}
