package com.esentri.rezeption.infrastructure.persistence.buchung;

import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungRepository;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import io.domainlifecycles.jdbc.imp.JdbcAggregateRepository;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BuchungRepositoryJdbc extends JdbcAggregateRepository<Buchung, BuchungsId> implements BuchungRepository {

    public BuchungRepositoryJdbc(JdbcDomainPersistenceProvider domainPersistenceProvider,
                                 PersistenceEventPublisher persistenceEventPublisher) {
        super(Buchung.class, domainPersistenceProvider, persistenceEventPublisher);
    }

    @Override
    public Optional<Buchung> findById(BuchungsId id) {
        return super.findById(id);
    }

    @Override
    public Buchung insert(Buchung buchung) {
        return super.insert(buchung);
    }

    @Override
    public Buchung update(Buchung buchung) {
        return super.update(buchung);
    }

    @Override
    public void delete(BuchungsId id) {
        super.deleteById(id);
    }

    @Override
    public Optional<Buchung> deleteById(BuchungsId id) {
        var deleted = findById(id);
        super.deleteById(id);
        return deleted;
    }
}
