package com.esentri.rezeption.core.domain.rechnung;

import io.domainlifecycles.domain.types.DomainCommand;

public record LadeRechnungPDF(Rechnung.Id rechnungsId) implements DomainCommand {
}
