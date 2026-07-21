package com.esentri.rezeption.infrastructure.persistence.zimmer;

import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import io.domainlifecycles.jooq.imp.JooqAggregateRepository;
import io.domainlifecycles.jooq.imp.provider.JooqDomainPersistenceProvider;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ZimmerRepositoryJooq extends JooqAggregateRepository<Zimmer, ZimmerId> implements ZimmerRepository {

    public ZimmerRepositoryJooq(DSLContext dslContext,
                                JooqDomainPersistenceProvider domainPersistenceProvider,
                                PersistenceEventPublisher persistenceEventPublisher) {
        super(Zimmer.class, dslContext, domainPersistenceProvider, persistenceEventPublisher);
    }

    @Override
    public Optional<Zimmer> findById(ZimmerId id) {
        return super.findById(id);
    }

    @Override
    public Zimmer insert(Zimmer zimmer) {
        return super.insert(zimmer);
    }

    @Override
    public Zimmer update(Zimmer zimmer) {
        return super.update(zimmer);
    }

    @Override
    public Optional<Zimmer> deleteById(ZimmerId id) {
        var deleted = findById(id);
        super.deleteById(id);
        return deleted;
    }
}
