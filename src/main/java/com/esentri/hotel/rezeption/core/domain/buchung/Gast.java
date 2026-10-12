package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.EntityBase;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Optional;
import java.util.UUID;

/**
 * Hauptgast einer Buchung. Kontaktdaten und Adresse koennen bei der Uebernahme der Reservierung noch fehlen;
 * sie werden beim CheckIn geprueft, korrigiert und vervollstaendigt.
 */
public final class Gast extends EntityBase<Gast.GastId> {

    public record GastId(@NotNull UUID value) implements Identity<UUID> {
    }

    @NotNull
    private final GastId id;

    @NotNull
    private Name name;

    private Email email;

    private Telefonnummer telefonnummer;

    private Adresse adresse;

    @Builder
    public Gast(long concurrencyVersion, GastId id, Name name, Email email, Telefonnummer telefonnummer,
                Adresse adresse) {
        super(concurrencyVersion);
        this.id = id;
        this.name = name;
        this.email = email;
        this.telefonnummer = telefonnummer;
        this.adresse = adresse;
    }

    @FactoryMethod
    static Gast erfasse(Name name, Email email, Telefonnummer telefonnummer, Adresse adresse) {
        return new Gast(0, new GastId(UUID.randomUUID()), name, email, telefonnummer, adresse);
    }

    /**
     * Uebernimmt die beim CheckIn geprueften, korrigierten und vervollstaendigten Gastdaten. Nur ueber die Buchung
     * aufrufbar.
     */
    void aktualisiere(Gastdaten gastdaten) {
        this.name = gastdaten.name();
        this.email = gastdaten.email();
        this.telefonnummer = gastdaten.telefonnummer();
        this.adresse = gastdaten.adresse();
    }

    @FactoryMethod
    static Gast erfasse(Gastdaten gastdaten) {
        return erfasse(gastdaten.name(), gastdaten.email(), gastdaten.telefonnummer(), gastdaten.adresse());
    }

    public Name getName() {
        return name;
    }

    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<Telefonnummer> getTelefonnummer() {
        return Optional.ofNullable(telefonnummer);
    }

    public Optional<Adresse> getAdresse() {
        return Optional.ofNullable(adresse);
    }
}
