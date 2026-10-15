package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.EntityBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Der Gast einer Buchung. Buchungen kommen ueber verschiedene Kanaele und bringen nicht immer
 * vollstaendige Gastdaten mit - Pflicht ist nur der Nachname.
 */
@Getter
public final class Gast extends EntityBase<Gast.GastId> {

    public record GastId(@NotNull UUID value) implements Identity<UUID> {
    }

    private final GastId id;

    @Size(max = 100)
    private final String vorname;

    @NotBlank
    @Size(max = 100)
    private final String nachname;

    @Builder
    public Gast(long concurrencyVersion, GastId id, String vorname, String nachname) {
        super(concurrencyVersion);
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
    }

    @FactoryMethod
    public static Gast neu(String vorname, String nachname) {
        return new Gast(0L, new GastId(UUID.randomUUID()), vorname, nachname);
    }
}
