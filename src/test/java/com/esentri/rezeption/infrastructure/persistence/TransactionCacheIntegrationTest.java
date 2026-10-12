package com.esentri.rezeption.infrastructure.persistence;

import com.esentri.rezeption.application.buchung.BuchungApplicationService;
import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungRepository;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.BuchungsStatus;
import com.esentri.rezeption.domain.buchung.CheckeGastAus;
import com.esentri.rezeption.domain.buchung.HauptGast;
import com.esentri.rezeption.domain.buchung.HauptGastId;
import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.persistence.cache.TransactionCacheProvider;
import io.domainlifecycles.persistence.spring.cache.SpringTransactionCacheProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Stellt sicher, dass der Transaction Cache von DLC mit der JDBC-Integration greift: Ein innerhalb einer
 * Transaktion geladenes Aggregate wird beim anschließenden {@code update} nicht erneut aus der Datenbank gelesen.
 * Dazu wird jedes vorbereitete SQL-Statement über einen Proxy um die {@link DataSource} mitgeschrieben.
 */
@SpringBootTest
class TransactionCacheIntegrationTest {

    @TestConfiguration
    static class StatementRecording {

        static final List<String> STATEMENTS = new CopyOnWriteArrayList<>();

        @Bean
        static BeanPostProcessor statementRecordingDataSource() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) {
                    return bean instanceof DataSource dataSource ? recording(dataSource) : bean;
                }
            };
        }

        private static DataSource recording(DataSource dataSource) {
            return (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(),
                new Class<?>[]{DataSource.class}, (proxy, method, args) -> {
                    Object result = invoke(dataSource, method, args);
                    return result instanceof Connection connection ? recording(connection) : result;
                });
        }

        private static Connection recording(Connection connection) {
            return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                    if (method.getName().startsWith("prepare") && args != null && args[0] instanceof String sql) {
                        STATEMENTS.add(sql);
                    }
                    return invoke(connection, method, args);
                });
        }

        private static Object invoke(Object target, java.lang.reflect.Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    @Autowired
    private ZimmerRepository zimmerRepository;

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private BuchungApplicationService buchungApplicationService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ApplicationContext applicationContext;

    private TransactionTemplate transaction;

    @BeforeEach
    void setUp() {
        transaction = new TransactionTemplate(transactionManager);
    }

    @Test
    void Should_UseTheJdbcTransactionCacheProvider_OnlyWithinATransaction() {
        // the JDBC integration is the only persistence backend: exactly its cache provider exists
        assertThat(applicationContext.getBeansOfType(TransactionCacheProvider.class))
            .containsOnlyKeys("dlcJdbcTransactionCacheProvider");
        TransactionCacheProvider<?> cacheProvider = applicationContext.getBean(TransactionCacheProvider.class);
        assertThat(cacheProvider).isInstanceOf(SpringTransactionCacheProvider.class);
        ZimmerId zimmerId = neuesZimmer();

        assertThat(cacheProvider.currentTransactionCache()).isEmpty();
        transaction.executeWithoutResult(status -> {
            zimmerRepository.findById(zimmerId).orElseThrow();
            assertThat(cacheProvider.currentTransactionCache()).isPresent();
        });
        assertThat(cacheProvider.currentTransactionCache()).isEmpty();
    }

    @Test
    void Should_NotReadTheAggregateAgain_When_ItIsUpdatedInTheTransactionItWasLoadedIn() {
        // given
        ZimmerId zimmerId = neuesZimmer();

        transaction.executeWithoutResult(status -> {
            Zimmer zimmer = zimmerRepository.findById(zimmerId).orElseThrow();
            belege(zimmer, 0);

            // when
            StatementRecording.STATEMENTS.clear();
            zimmerRepository.update(zimmer);

            // then: the state loaded before comes from the cache
            assertThat(selectsFrom("ZIMMER")).isZero();
            assertThat(selectsFrom("ZIMMER_BELEGUNGEN")).isZero();
        });

        assertThat(zimmerRepository.findById(zimmerId).orElseThrow().getBelegungen()).hasSize(1);
    }

    @Test
    void Should_ReadTheAggregateAgain_When_ItIsUpdatedWithoutATransaction() {
        // given
        ZimmerId zimmerId = neuesZimmer();
        Zimmer zimmer = zimmerRepository.findById(zimmerId).orElseThrow();
        belege(zimmer, 0);

        // when
        StatementRecording.STATEMENTS.clear();
        zimmerRepository.update(zimmer);

        // then: without a transaction there is no cache, the current state is read from the database
        assertThat(selectsFrom("ZIMMER")).isEqualTo(1);
        assertThat(selectsFrom("ZIMMER_BELEGUNGEN")).isEqualTo(1);
    }

    @Test
    void Should_StartEveryTransactionWithAnEmptyCache() {
        // given: loaded in one transaction ...
        ZimmerId zimmerId = neuesZimmer();
        Zimmer zimmer = transaction.execute(status -> zimmerRepository.findById(zimmerId).orElseThrow());
        belege(zimmer, 0);

        // when: ... updated in the next one
        StatementRecording.STATEMENTS.clear();
        transaction.executeWithoutResult(status -> zimmerRepository.update(zimmer));

        // then: nothing cached is carried over
        assertThat(selectsFrom("ZIMMER")).isEqualTo(1);
    }

    @Test
    void Should_ReadTheBuchungOnce_When_TheGuestChecksOut() {
        // given
        BuchungsId buchungsId = eingecheckteBuchung();

        // when: the transactional application service loads and updates the Buchung
        StatementRecording.STATEMENTS.clear();
        buchungApplicationService.checkeGastAus(new CheckeGastAus(buchungsId));

        // then: only its findById reads the aggregate, its update takes the cached state
        assertThat(selectsFrom("BUCHUNG")).isEqualTo(1);
        assertThat(selectsFrom("HAUPT_GAST")).isEqualTo(1);
        assertThat(buchungRepository.findById(buchungsId).orElseThrow().getStatus())
            .isEqualTo(BuchungsStatus.AUSGECHECKT);
    }

    private ZimmerId neuesZimmer() {
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        zimmerRepository.insert(new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD));
        return zimmerId;
    }

    private BuchungsId eingecheckteBuchung() {
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        transaction.executeWithoutResult(status -> {
            Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD);
            zimmer.belegeFuer(buchungsId, zeitraum);
            zimmerRepository.insert(zimmer);
            HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
            Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);
            buchung.checkeEin(zimmerId);
            buchungRepository.insert(buchung);
        });
        return buchungsId;
    }

    private static void belege(Zimmer zimmer, int inTagen) {
        LocalDate start = LocalDate.now().plusDays(inTagen);
        zimmer.belegeFuer(new BuchungsId(UUID.randomUUID()), new Zeitraum(start, start.plusDays(1)));
    }

    /**
     * The number of recorded SELECTs reading the given table.
     */
    private static long selectsFrom(String table) {
        Pattern fromTable = Pattern.compile("\\bFROM\\s+(\\S+\\.)?\"?" + table + "\"?(\\s|$)");
        return StatementRecording.STATEMENTS.stream()
            .map(sql -> sql.toUpperCase(Locale.ROOT).trim())
            .filter(sql -> sql.startsWith("SELECT"))
            .filter(sql -> fromTable.matcher(sql).find())
            .count();
    }
}
