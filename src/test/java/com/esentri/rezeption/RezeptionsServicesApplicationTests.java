/*
 *  Copyright 2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.esentri.rezeption;

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.WartungsPlanungId;
import com.esentri.rezeption.core.domain.rechnung.ErstelleRechnungFuerBuchung;
import com.esentri.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.rezeption.core.domain.buchung.VervollstaendigeGastDaten;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungEingeplant;
import com.esentri.rezeption.core.inport.BuchungUseCases;
import com.esentri.rezeption.core.inport.RechnungUseCases;
import com.esentri.rezeption.core.inport.ZimmerUseCases;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.Buchungen;
import com.esentri.rezeption.core.outport.ServiceLeistungen;
import com.esentri.rezeption.core.outport.ZimmerVerwaltung;
import com.esentri.rezeption.outbound.TestDataIds;
import com.esentri.rezeption.outbound.ZimmerAuslastungenImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Dies ist ein JUnit-Test für den Application Service "RezeptionsServices".
 *
 * Author: Mario Herb
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RezeptionsServicesApplicationTests {

	@Autowired
	private ZimmerAuslastungenImpl zimmerAuslastungenImpl;

	@Autowired
	private BuchungUseCases buchungUseCases;

	@Autowired
	private RechnungUseCases rechnungUseCases;

	@Autowired
	private ZimmerUseCases zimmerUseCases;

	@Autowired
	private Buchungen buchungen;

	@Autowired
	private ZimmerVerwaltung zimmerVerwaltung;

	@Autowired
	private ServiceLeistungen serviceLeistungen;

	@Autowired
	private DomainEventPublisher domainEventPublisher;

	@Test
	@Order(1)
	void contextLoads() {
	}

	@Test
	@Order(2)
	void testVerfuegbareZimmer(){
		var zimmer = zimmerUseCases.verfuegbareZimmer(
				TestDataIds.HOTEL_ID.id(),
				LocalDate.now(),
				LocalDate.now().plusDays(2),
				ZimmerKategorie.PRESIDENTIAL_SUITE,
				4
		);
		assertThat(zimmer).hasSize(1);
	}


	@Test
	@Order(3)
	void testCheckInFailGeburtsdatumGast(){
		assertThatThrownBy(()->
		buchungUseCases.handle(new CheckeEin(
				TestDataIds.BUCHUNG_ID_OFFEN.id(),
				TestDataIds.ZIMMER_ID_BUSINESS_SUITE.id(),
				3
		))).hasMessageContaining("Geburtsdatum");
	}

	@Test
	@Order(4)
	void testCheckInErfolgreich(){
		buchungUseCases.handle(new VervollstaendigeGastDaten(
				TestDataIds.BUCHUNG_ID_OFFEN.id(),
				"Chuck",
				"Bubu",
				LocalDate.of(1980,1,1),
				null,
				null,
				new Adresse("Am Platz", "1", "55555", "Muggelhausen")
		));
		buchungUseCases.handle(new CheckeEin(
				TestDataIds.BUCHUNG_ID_OFFEN.id(),
				TestDataIds.ZIMMER_ID_BUSINESS_SUITE.id(),
				3
		));
		var res = buchungen.findById(TestDataIds.BUCHUNG_ID_OFFEN.id());
		assertThat(res).isPresent();
		assertThat(res.get().getCheckInAm().toLocalDate()).isEqualTo(LocalDate.now());

		var zimmer = zimmerVerwaltung.findById(res.get().getZimmerNummer()).orElseThrow();
		assertThat(zimmer.aktuelleBelegung()).isPresent();
		var belegung = zimmer.aktuelleBelegung().get();
		assertThat(belegung.belegungTyp()).isEqualTo(BelegungTyp.GAST);
		assertThat(belegung.von()).isEqualTo(LocalDate.now());
		assertThat(belegung.bis()).isEqualTo(LocalDate.now().plusDays(res.get().getGeplanteAnzahlNaechte()));

	}

	@Test
	@Order(5)
	void testCheckInFailDoppelt(){
		buchungUseCases.handle(new VervollstaendigeGastDaten(
				TestDataIds.BUCHUNG_ID_OFFEN.id(),
				"Chuck",
				"Bubu",
				LocalDate.of(1980,1,1),
				null,
				null,
				new Adresse("Am Platz", "1", "55555", "Muggelhausen")
		));

		assertThatThrownBy( () ->{
			buchungUseCases.handle(new CheckeEin(
					TestDataIds.BUCHUNG_ID_OFFEN.id(),
					TestDataIds.ZIMMER_ID_BUSINESS_SUITE.id(),
					3
			));
			buchungUseCases.handle(new CheckeEin(
					TestDataIds.BUCHUNG_ID_OFFEN.id(),
					TestDataIds.ZIMMER_ID_BUSINESS_SUITE.id(),
					3
			));
		}).hasMessageContaining("bereits eingecheckt");
	}

	@Test
	@Order(6)
	void testReadModel(){
		var auslastung = zimmerAuslastungenImpl.auslastung(TestDataIds.HOTEL_ID.id(), LocalDate.now(), LocalDate.now().plusDays(5));

		var auslastungHeuteEinzelzimmer = auslastung.stream()
				.filter(a -> a.vom().isEqual(LocalDate.now()))
				.filter(a -> a.zimmerKategorie().equals(ZimmerKategorie.EINZELZIMMER))
				.toList();
		assertThat(auslastungHeuteEinzelzimmer).hasSize(1);
		assertThat(auslastungHeuteEinzelzimmer.get(0).anzahlGesamt()).isEqualTo(1);
		assertThat(auslastungHeuteEinzelzimmer.get(0).anzahlBelegt()).isEqualTo(1);

		var auslastungNachBelegungEinzelzimmer = auslastung.stream()
				.filter(a -> a.vom().isEqual(LocalDate.now().plusDays(4)))
				.filter(a -> a.zimmerKategorie().equals(ZimmerKategorie.EINZELZIMMER))
				.toList();
		assertThat(auslastungNachBelegungEinzelzimmer).hasSize(1);
		assertThat(auslastungNachBelegungEinzelzimmer.get(0).anzahlGesamt()).isEqualTo(1);
		assertThat(auslastungNachBelegungEinzelzimmer.get(0).anzahlBelegt()).isEqualTo(0);


		var auslastungHeutePresidentialSuite = auslastung.stream()
				.filter(a -> a.vom().isEqual(LocalDate.now()))
				.filter(a -> a.zimmerKategorie().equals(ZimmerKategorie.PRESIDENTIAL_SUITE))
				.toList();
		assertThat(auslastungHeutePresidentialSuite).hasSize(1);
		assertThat(auslastungHeutePresidentialSuite.get(0).anzahlGesamt()).isEqualTo(1);
		assertThat(auslastungHeutePresidentialSuite.get(0).anzahlBelegt()).isEqualTo(0);

		var auslastungHeuteBusinessSuite = auslastung.stream()
				.filter(a -> a.vom().isEqual(LocalDate.now()))
				.filter(a -> a.zimmerKategorie().equals(ZimmerKategorie.BUSINESS_SUITE))
				.toList();
		assertThat(auslastungHeuteBusinessSuite).hasSize(1);
		assertThat(auslastungHeuteBusinessSuite.get(0).anzahlGesamt()).isEqualTo(1);
		assertThat(auslastungHeuteBusinessSuite.get(0).anzahlBelegt()).isEqualTo(1);
	}

	@Test
	@Order(7)
	void testCheckOutFailKeineAbrechnung(){
		assertThatThrownBy(()->{
			buchungUseCases.handle(new CheckeAus(
					TestDataIds.BUCHUNG_ID_EINGECHECKT.id()
			));
		}).hasMessageContaining("Zimmerabrechnung");
	}

	@Test
	@Order(8)
	void testCheckOutErfolgreich(){
		var res = buchungen.findById(TestDataIds.BUCHUNG_ID_EINGECHECKT.id());
		assertThat(res).isPresent();

		var serviceLeistungenZumAbrechnen = serviceLeistungen.find(TestDataIds.BUCHUNG_ID_EINGECHECKT.id());

		rechnungUseCases.handle(new ErstelleRechnungFuerBuchung(
				TestDataIds.BUCHUNG_ID_EINGECHECKT.id(),
				serviceLeistungenZumAbrechnen.stream().map(ServiceLeistung::id).toList(),
				res.get().getGast().getHeimAdresse()
		));

		buchungUseCases.handle(new CheckeAus(
			TestDataIds.BUCHUNG_ID_EINGECHECKT.id()
		));

		assertThat(res.get().getCheckOutAm().toLocalDate()).isEqualTo(LocalDate.now());

		var zimmer = zimmerVerwaltung.findById(res.get().getZimmerNummer()).orElseThrow();
		assertThat(zimmer.aktuelleBelegung()).isEmpty();

		serviceLeistungenZumAbrechnen.forEach(sl -> assertThat(sl.getAbgerechnetPer()).isNotNull());
	}

	@Test
	@Order(9)
	void testWartungErfolgreich(){
		var wartungGeplant = new ZimmerWartungEingeplant(
				new WartungsPlanungId(UUID.randomUUID().toString()),
				LocalDate.now(),
				LocalDate.now().plusDays(3),
				List.of(TestDataIds.ZIMMER_ID_PRESIDENTIAL_SUITE.id())
		);
		domainEventPublisher.publish(wartungGeplant);

		var zimmer = zimmerVerwaltung.findById(TestDataIds.ZIMMER_ID_PRESIDENTIAL_SUITE.id()).orElseThrow();
		assertThat(zimmer.aktuelleBelegung()).isPresent();
		assertThat(zimmer.aktuelleBelegung().get().belegungTyp()).isEqualTo(BelegungTyp.WARTUNG);


	}



}
