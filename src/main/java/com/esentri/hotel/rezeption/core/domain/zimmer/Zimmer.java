package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.AggregateRootBase;
import io.domainlifecycles.validation.extend.Query;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Zimmer eines Hotels. Die {@link ZimmerId} ist global eindeutig, die {@link Zimmernummer} nur innerhalb des Hotels.
 * Die {@link Belegung}en des Zimmers (Gaeste, Wartung) ueberschneiden sich zeitlich nie.
 */
public final class Zimmer extends AggregateRootBase<Zimmer.ZimmerId> {

    public record ZimmerId(@NotNull UUID value) implements Identity<UUID> {
    }

    @NotNull
    private final ZimmerId id;

    @NotNull
    private final HotelId hotelId;

    @NotNull
    private final Zimmernummer zimmernummer;

    @NotNull
    private final Zimmerkategorie kategorie;

    @NotNull
    private final List<Belegung> belegungen;

    @Builder
    public Zimmer(long concurrencyVersion, ZimmerId id, HotelId hotelId, Zimmernummer zimmernummer,
                  Zimmerkategorie kategorie, List<Belegung> belegungen) {
        super(concurrencyVersion);
        this.id = id;
        this.hotelId = hotelId;
        this.zimmernummer = zimmernummer;
        this.kategorie = kategorie;
        this.belegungen = belegungen == null ? new ArrayList<>() : new ArrayList<>(belegungen);
    }

    @FactoryMethod
    public static Zimmer legeAn(HotelId hotelId, Zimmernummer zimmernummer, Zimmerkategorie kategorie) {
        return new Zimmer(0, new ZimmerId(UUID.randomUUID()), hotelId, zimmernummer, kategorie, List.of());
    }

    @Override
    public void validate() {
        for (int i = 0; i < belegungen.size(); i++) {
            for (int j = i + 1; j < belegungen.size(); j++) {
                DomainAssertions.isFalse(
                    belegungen.get(i).getZeitraum().ueberschneidetSich(belegungen.get(j).getZeitraum()),
                    "Die Belegungen des Zimmers %s ueberschneiden sich.".formatted(zimmernummer.value()));
            }
        }
    }

    /**
     * Belegt das Zimmer fuer den Aufenthalt einer Buchung, die gerade eincheckt. Nur moeglich, wenn das Zimmer zum
     * Hotel der Buchung gehoert und im gesamten Zeitraum durch nichts anderes belegt ist - weder durch Gaeste noch
     * durch Wartung.
     */
    public Belegung.BelegungId belegeDurchGast(Buchung.BuchungsId buchungsId, HotelId hotelDerBuchung,
                                               Belegungszeitraum zeitraum) {
        DomainAssertions.isTrue(hotelId.equals(hotelDerBuchung),
            "Das Zimmer %s gehoert nicht zum Hotel der Buchung.".formatted(zimmernummer.value()));
        return belege(Belegung.durchGast(buchungsId, zeitraum));
    }

    public Belegung.BelegungId planeWartung(Wartungsgrund wartungsgrund, Belegungszeitraum zeitraum) {
        return belege(Belegung.durchWartung(wartungsgrund, zeitraum));
    }

    /**
     * Beendet eine Wartung: eine laufende Wartung endet heute, eine noch nicht begonnene entfaellt.
     */
    public Zimmer beendeWartung(Belegung.BelegungId belegungId, LocalDate heute) {
        var wartung = belegungen.stream()
            .filter(b -> b.id().equals(belegungId) && b.getArt() == Belegungsart.WARTUNG)
            .findFirst();
        DomainAssertions.isTrue(wartung.isPresent(),
            "Am Zimmer %s ist diese Wartung nicht geplant.".formatted(zimmernummer.value()));
        var zeitraum = wartung.get().getZeitraum();
        DomainAssertions.isTrue(heute.isBefore(zeitraum.bis()), "Die Wartung ist bereits beendet.");
        if (zeitraum.von().isBefore(heute)) {
            wartung.get().endetAm(heute);
        } else {
            belegungen.remove(wartung.get());
        }
        return this;
    }

    /**
     * Der Gast der Buchung reist ab. Bei vorzeitiger Abreise endet seine Belegung heute - am Anreisetag selbst
     * entfaellt sie ganz; am geplanten Abreisetag oder spaeter endet sie ohnehin schon.
     */
    public Zimmer beendeGastBelegung(Buchung.BuchungsId buchungsId, LocalDate heute) {
        var gastBelegung = belegungen.stream()
            .filter(b -> b.getBuchungsId().filter(buchungsId::equals).isPresent())
            .findFirst();
        DomainAssertions.isTrue(gastBelegung.isPresent(),
            "Das Zimmer %s ist nicht durch diese Buchung belegt.".formatted(zimmernummer.value()));
        var zeitraum = gastBelegung.get().getZeitraum();
        if (!heute.isBefore(zeitraum.bis())) {
            return this;
        }
        if (zeitraum.von().isBefore(heute)) {
            gastBelegung.get().endetAm(heute);
        } else {
            belegungen.remove(gastBelegung.get());
        }
        return this;
    }

    private Belegung.BelegungId belege(Belegung neueBelegung) {
        var konflikt = belegungen.stream()
            .filter(b -> b.getZeitraum().ueberschneidetSich(neueBelegung.getZeitraum()))
            .findFirst();
        DomainAssertions.isTrue(konflikt.isEmpty(), konflikt
            .map(b -> "Das Zimmer %s ist vom %s bis %s bereits belegt (%s)."
                .formatted(zimmernummer.value(), b.getZeitraum().von(), b.getZeitraum().bis(), b.getArt()))
            .orElse(""));
        belegungen.add(neueBelegung);
        return neueBelegung.id();
    }

    @Query
    public boolean istFrei(Belegungszeitraum zeitraum) {
        return belegungen.stream().noneMatch(b -> b.getZeitraum().ueberschneidetSich(zeitraum));
    }

    public HotelId getHotelId() {
        return hotelId;
    }

    public Zimmernummer getZimmernummer() {
        return zimmernummer;
    }

    public Zimmerkategorie getKategorie() {
        return kategorie;
    }

    public List<Belegung> getBelegungen() {
        return Collections.unmodifiableList(belegungen);
    }
}
