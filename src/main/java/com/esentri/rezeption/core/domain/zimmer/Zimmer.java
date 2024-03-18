package com.esentri.rezeption.core.domain.zimmer;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nitrox.dlc.domain.types.AggregateRoot;
import nitrox.dlc.domain.types.Identity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Zimmerklasse, implementiert als AggregateRoot mit ZimmerNummer, Kapazität und Stockwerk und Belegungen.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class Zimmer implements AggregateRoot<Zimmer.ZimmerNummer> {

    /**
     * Innere Klasse zur Repräsentation der ZimmerNummer.
     */
    public record ZimmerNummer(String value) implements Identity<String> {}

    /**
     * Eindeutige Nummer des Zimmers.
     */
    @EqualsAndHashCode.Include
    private final ZimmerNummer nummer;

    /**
     * Eindeutige Hotel-ID, zu dem das Zimmer gehört.
     */
    private final Hotel.HotelId hotelId;

    /**
     * Kategorie des Zimmers.
     */
    private final ZimmerKategorie kategorie;

    /**
     * Kapazität des Zimmers.
     */
    private final int kapazitaet;

    /**
     * Stockwerk, in dem sich das Zimmer befindet.
     */
    private final int stockwerk;

    /**
     * Liste der Belegungen des Zimmers.
     */
    private final List<Belegung> belegungen;

    private long concurrencyVersion;

    /**
     * Konstruktor für die Erstellung eines Zimmer-Objekts.
     *
     * @param nummer Die eindeutige Nummer des Zimmers.
     * @param hotelId Die eindeutige ID des Hotels, zu dem das Zimmer gehört.
     * @param kategorie Die Kategorie des Zimmers.
     * @param kapazitaet Die Kapazität des Zimmers.
     * @param stockwerk Das Stockwerk, in dem sich das Zimmer befindet.
     */
    public Zimmer(ZimmerNummer nummer, Hotel.HotelId hotelId, ZimmerKategorie kategorie, int kapazitaet, int stockwerk) {
        validateKapazitaet(kapazitaet);
        if (nummer == null) {
            throw new IllegalArgumentException("Die Nummer des Zimmers darf nicht null sein");
        }
        if (hotelId == null) {
            throw new IllegalArgumentException("Die HotelId darf nicht null sein");
        }
        if (kategorie == null) {
            throw new IllegalArgumentException("Die ZimmerKategorie darf nicht null sein");
        }
        this.nummer = nummer;
        this.hotelId = hotelId;
        this.kategorie = kategorie;
        this.kapazitaet = kapazitaet;
        this.stockwerk = stockwerk;
        belegungen = new ArrayList<>();
    }

    /**
     * Private Methode zur Überprüfung, ob die Kapazität korrekt ist.
     *
     * @param kapazitaet Die Kapazität des Zimmers.
     */
    private void validateKapazitaet(int kapazitaet) {
        if (kapazitaet <= 0) {
            throw new IllegalArgumentException("Die Kapazität muss >0 sein");
        }
    }

    /**
     * Gibt die eindeutige ZimmerNummer zurück, die als ID des Zimmers dient.
     *
     * @return Eindeutige ZimmerNummer.
     */
    @Override
    public ZimmerNummer id() {
        return nummer;
    }

    /**
     * Überprüft, ob das Zimmer eine überschneidende Belegung aufweist.
     *
     * @param probeBelegung Die zu prüfende Belegung.
     * @return true, wenn eine überschneidende Belegung vorhanden ist, sonst false.
     */
    public boolean hatUeberschneidendeBelegung(Belegung probeBelegung){
        return belegungen.stream().anyMatch(b -> b.ueberschneidetZeitraum(probeBelegung.von(), probeBelegung.bis()));
    }

    /**
     * Fügt eine neue Belegung hinzu, wenn keine Überschneidung vorliegt.
     *
     * @param von Anfangsdatum der Belegung.
     * @param bis Enddatum der Belegung.
     * @param belegungTyp Typ der Belegung.
     * @return true, wenn eine neue Belegung hinzugefügt wurde, sonst false.
     */
    public boolean neueBelegung(LocalDate von, LocalDate bis, BelegungTyp belegungTyp){
        var neueBelegung = new Belegung(von, bis, belegungTyp);
        if(!hatUeberschneidendeBelegung(neueBelegung)){
            belegungen.add(neueBelegung);
            return true;
        }
        return false;
    }

    /**
     * Gibt die aktuelle Belegung des Zimmers zurück.
     *
     * @return Optional der aktuellen Belegung.
     */
    public Optional<Belegung> aktuelleBelegung(){
        return belegungen
                .stream()
                .filter(b -> b.istBelegtAm(LocalDate.now()))
                .findFirst();
    }

    /**
     * Beendet die aktuelle Belegung des Zimmers.
     *
     * @return Aktualisiertes Zimmerobjekt.
     */
    public Zimmer aktuelleBelegungBeenden(){
        var aktuellOptional = aktuelleBelegung();
        if(aktuellOptional.isPresent()){
            var aktuell = aktuellOptional.get();
            belegungen.remove(aktuell);
        }
        return this;
    }

    /**
     * Gibt nicht modifizerbare Liste der Zimmer Belegungen zurück.
     */
    public List<Belegung> getBelegungen() {
        return Collections.unmodifiableList(belegungen);
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }
}