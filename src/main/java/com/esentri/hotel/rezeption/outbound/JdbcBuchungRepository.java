package com.esentri.hotel.rezeption.outbound;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import io.domainlifecycles.jdbc.imp.JdbcAggregateRepository;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;

class JdbcBuchungRepository extends JdbcAggregateRepository<Buchung, Buchung.BuchungId> implements BuchungRepository {

    JdbcBuchungRepository(JdbcDomainPersistenceProvider provider, PersistenceEventPublisher publisher) {
        super(Buchung.class, provider, publisher);
    }
}
