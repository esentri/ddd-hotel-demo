package com.esentri.rezeption.domain.model.rechnung;

import com.esentri.rezeption.domain.model.buchung.BuchungId;
import io.domainlifecycles.domain.types.AggregateRoot;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rechnung implements AggregateRoot<RechnungId> {

    @EqualsAndHashCode.Include
    @NotNull
    private RechnungId id;

    @NotNull
    private BuchungId buchungId;

    @NotNull
    @Positive
    private BigDecimal nettoBetrag;

    @NotNull
    @Positive
    private BigDecimal bruttoBetrag;

    public Rechnung(RechnungId id, BuchungId buchungId, BigDecimal nettoBetrag, BigDecimal bruttoBetrag) {
        this.id = id;
        this.buchungId = buchungId;
        this.nettoBetrag = nettoBetrag;
        this.bruttoBetrag = bruttoBetrag;
    }

    @Override
    public RechnungId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return 0;
    }
}
