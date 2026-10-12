package com.esentri.hotel.rezeption.outbound;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungsStatus;
import com.esentri.hotel.rezeption.core.domain.buchung.Reservierungsnummer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.jdbc.imp.JdbcAggregateRepository;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.jdbc.util.JdbcRecordMapper;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;

import java.util.List;
import java.util.Optional;

public class JdbcBuchungRepository extends JdbcAggregateRepository<Buchung, Buchung.BuchungsId>
        implements BuchungRepository {

    private final JdbcDomainPersistenceProvider provider;

    public JdbcBuchungRepository(JdbcDomainPersistenceProvider domainPersistenceProvider,
                                 PersistenceEventPublisher persistenceEventPublisher) {
        super(Buchung.class, domainPersistenceProvider, persistenceEventPublisher);
        this.provider = domainPersistenceProvider;
    }

    @Override
    public Optional<Buchung> findByReservierungsnummer(Reservierungsnummer reservierungsnummer) {
        var buchung = provider.schemaMetadata.table(provider.entityRecordType(Buchung.class.getName()));
        var buchungRecord = JdbcRecordMapper.selectOneByColumn(provider.connectionProvider, provider.dialect, buchung,
            buchung.column("reservierungsnummer_value").name(), reservierungsnummer.value());
        return Optional.ofNullable(buchungRecord).flatMap(r -> getFetcher().fetchDeep(r).resultValue());
    }

    @Override
    public List<Buchung> findEingecheckteBuchungen(Zimmer.ZimmerId zimmerId) {
        var buchung = provider.schemaMetadata.table(provider.entityRecordType(Buchung.class.getName()));
        return JdbcRecordMapper.selectWithSql(provider.connectionProvider, buchung,
                "SELECT * FROM %s WHERE zimmer_id = ? AND status = ?".formatted(buchung.qualifiedName()),
                zimmerId.value(), BuchungsStatus.EINGECHECKT.name())
            .stream()
            .flatMap(r -> getFetcher().fetchDeep(r).resultValue().stream())
            .toList();
    }
}
