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
import com.esentri.rezeption.core.inport.RechnungDriver;
import com.esentri.rezeption.core.inport.ReservierungDriver;
import com.esentri.rezeption.core.inport.ServiceLeistungDriver;
import com.esentri.rezeption.core.inport.ZimmerDriver;
import nitrox.dlc.diagram.domain.DomainDiagramGenerator;
import nitrox.dlc.diagram.domain.config.DomainDiagramConfig;
import nitrox.dlc.mirror.api.Domain;
import nitrox.dlc.mirror.reflect.ReflectiveDomainMirrorFactory;
import nitrox.dlc.mirror.resolver.TypeMetaResolver;
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
                .withShowQueryClients(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ZimmerDriver.class.getName()))
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
                .withShowQueryClients(true)
                .withShowReadModels(true)
                .withShowReadModelMethods(false)
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ZimmerDriver.class.getName()))
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
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ReservierungDriver.class.getName()))
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
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(RechnungDriver.class.getName()))
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
                .withTransitiveFilterSeedDomainServiceTypeNames(List.of(ServiceLeistungDriver.class.getName()))
                .build();
        DomainDiagramGenerator generator = new DomainDiagramGenerator(
                diagramConfig);

        String actualDiagramText = generator.generateDiagramText();
        System.out.println("Diagram:\n" + actualDiagramText);
    }


}
