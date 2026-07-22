package com.esentri.rezeption.infrastructure.persistence.zimmer;

import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import com.esentri.rezeption.records.Rezeption;
import io.domainlifecycles.jooq.imp.JooqAggregateRepository;
import io.domainlifecycles.jooq.imp.provider.JooqDomainPersistenceProvider;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.esentri.rezeption.records.Rezeption.REZEPTION;
import static com.esentri.rezeption.records.tables.Zimmer.ZIMMER;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;

@Repository
public class ZimmerRepositoryJooq extends JooqAggregateRepository<Zimmer, ZimmerId> implements ZimmerRepository {

    private final DSLContext dslContext;

    public ZimmerRepositoryJooq(DSLContext dslContext,
                                JooqDomainPersistenceProvider domainPersistenceProvider,
                                PersistenceEventPublisher persistenceEventPublisher) {
        super(Zimmer.class, dslContext, domainPersistenceProvider, persistenceEventPublisher);
        this.dslContext = dslContext;
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
    public List<Zimmer> findByKategorie(Zimmerkategorie kategorie) {
        return dslContext.select()
            .from(REZEPTION.ZIMMER)
            .where(ZIMMER.KATEGORIE.eq(kategorie.name()))
            .fetch()
            .map(r-> getFetcher().fetchDeep((UpdatableRecord<?>) r).resultValue().get());
    }

    @Override
    public Optional<Zimmer> deleteById(ZimmerId id) {
        var deleted = findById(id);
        super.deleteById(id);
        return deleted;
    }
}
