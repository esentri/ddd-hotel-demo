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

import com.esentri.rezeption.core.base.BaseInMemoryRepository;
import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.rechnung.ErstelleRechnungFuerBuchung;
import com.esentri.rezeption.core.domain.rechnung.ErstelleServiceRechnung;
import com.esentri.rezeption.core.domain.rechnung.MarkiereRechnungBezahlt;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.inport.BuchungUseCases;
import com.esentri.rezeption.core.inport.RechnungUseCases;
import com.esentri.rezeption.core.inport.ServiceLeistungUseCases;
import com.esentri.rezeption.core.inport.ZimmerUseCases;
import io.domainlifecycles.diagram.domain.DomainDiagramGenerator;
import io.domainlifecycles.diagram.domain.config.DomainDiagramConfig;
import io.domainlifecycles.mirror.api.Domain;
import io.domainlifecycles.mirror.reflect.ReflectiveDomainMirrorFactory;
import io.domainlifecycles.mirror.resolver.TypeMetaResolver;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

@Disabled
public class GenerateDiagramTest {

    @Test
    void generateDiagramForAppComplete() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(BaseInMemoryRepository.class.getName()))
                .withShowFields(false)
                .withShowMethods(false)
                .withCallApplicationServiceDriver(false)
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramPDFErstellung() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(
                        BaseInMemoryRepository.class.getName(),
                        ErstelleRechnungFuerBuchung.class.getName(),
                        ErstelleServiceRechnung.class.getName(),
                        MarkiereRechnungBezahlt.class.getName(),
                        Buchung.class.getName(),
                        Rechnung.class.getName(),
                        ServiceLeistung.class.getName()
                ))

                .withShowFields(false)
                .withShowMethods(false)
                .withCallApplicationServiceDriver(false)
                .withShowDomainEvents(false)
                .withShowRepositories(false)
                .withShowReadModels(true)
                .withShowDomainServices(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(RechnungUseCases.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramForAppCompleteAggregatesOnly() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(BaseInMemoryRepository.class.getName()))
                .withShowFields(true)
                .withShowMethods(true)
                .withShowApplicationServices(false)
                .withShowDomainEvents(false)
                .withShowDomainCommands(false)
                .withShowReadModels(false)
                .withShowDomainServices(false)
                .withShowRepositories(false)
                .withShowQueryHandlers(false)
                .withShowOutboundServices(false)
                .withCallApplicationServiceDriver(false)
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramForAppZimmerUseCases() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(BaseInMemoryRepository.class.getName()))
                .withShowFields(false)
                .withShowMethods(true)
                .withShowReadModels(false)
                .withCallApplicationServiceDriver(false)
                .withShowQueryHandlers(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ZimmerUseCases.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramForAppZimmerAuslastung() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(
                        BaseInMemoryRepository.class.getName()
                ))
                .withShowFields(true)
                .withShowMethods(true)
                .withShowApplicationServiceMethods(false)
                .withShowDomainEvents(false)
                .withShowDomainCommands(false)
                .withShowRepositories(false)
                .withCallApplicationServiceDriver(false)
                .withShowQueryHandlers(true)
                .withShowReadModels(true)
                .withShowReadModelMethods(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ZimmerUseCases.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramForAppBuchungUseCases() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(
                        BaseInMemoryRepository.class.getName()
                ))
                .withShowFields(false)
                .withShowMethods(true)
                .withCallApplicationServiceDriver(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(BuchungUseCases.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramForAppRechnungUseCases() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(
                        BaseInMemoryRepository.class.getName()
                ))
                .withShowFields(false)
                .withShowMethods(false)
                .withCallApplicationServiceDriver(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(RechnungUseCases.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramForAppServiceLeistungUseCases() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(
                        BaseInMemoryRepository.class.getName()
                ))
                .withShowFields(false)
                .withShowMethods(false)
                .withCallApplicationServiceDriver(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ServiceLeistungUseCases.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }


}
