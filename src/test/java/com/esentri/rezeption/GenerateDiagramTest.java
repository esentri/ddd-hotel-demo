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
import com.esentri.rezeption.core.domain.zimmer.Belegung;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.inport.RechnungUseCases;
import com.esentri.rezeption.core.inport.ReservierungUseCases;
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
    void generateDiagramForAppZimmerDriver() {
        Domain.setGenericTypeResolver(new TypeMetaResolver());
        Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.rezeption"));


        DomainDiagramConfig diagramConfig = DomainDiagramConfig.builder()
                .withContextPackageName("com.esentri.rezeption")
                .withClassesBlacklist(List.of(BaseInMemoryRepository.class.getName()))
                .withShowFields(false)
                .withShowMethods(false)
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
                        BaseInMemoryRepository.class.getName(),
                        Zimmer.class.getName(),
                        Belegung.class.getName()
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
    void generateDiagramForAppReservierungDriver() {
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
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ReservierungUseCases.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }

    @Test
    void generateDiagramForAppRechnungDriver() {
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
    void generateDiagramForAppServiceLeistungDriver() {
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
