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

package com.esentri.rezeption.core.base;


import nitrox.dlc.domain.types.AggregateRoot;
import nitrox.dlc.domain.types.Identity;
import nitrox.dlc.domain.types.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Eine Basisklasse für die In-Memory-Repository-Implementierung.
 * Jedes Repository-Objekt enthält eine Liste von Aggregatwurzeln mit ihrem eindeutigen Identifikator.
 *
 * @author Mario Herb
 * @param <ID>, die Identität der AggregateRoot, ist generisch und erweitert die Identity-Schnittstelle.
 * @param <A>, die AggregateRoot ist generisch.
 */
public class BaseInMemoryRepository<ID extends Identity<?>, A extends AggregateRoot<ID>> implements Repository<ID, A> {

    protected final List<A> allAggregates;

    /**
     * Erstellt ein BaseInMemoryRepository-Objekt mit gegebener Aggregatliste.
     *
     * @param allAggregates, die Liste der Aggregatwurzeln.
     * @throws NullPointerException, wenn die gegebene Liste null ist.
     */
    public BaseInMemoryRepository(List<A> allAggregates) {
        Objects.requireNonNull(allAggregates);
        this.allAggregates = allAggregates;
    }

    /**
     * Findet das erste Vorkommen der AggregateRoot mit gegebener ID.
     *
     * @param id, die Identität des zu findenden AggregateRoot.
     * @return  Optional von AggregateRoot.
     */
    @Override
    public Optional<A> findById(ID id) {
        return allAggregates.stream().filter(a -> a.id().equals(id)).findFirst();
    }

    /**
     * Fügt eine neue AggregateRoot der Liste hinzu.
     *
     * @param aggregateRoot, die hinzuzufügende AggregateRoot.
     * @return  Die hinzugefügte AggregateRoot.
     */
    @Override
    public A insert(A aggregateRoot) {
        allAggregates.add(aggregateRoot);
        return aggregateRoot;
    }

    /**
     * Aktualisiert eine vorhandene AggregateRoot in der Liste.
     *
     * @param aggregateRoot, die zu aktualisierende AggregateRoot.
     * @return  Die aktualisierte AggregateRoot.
     */
    @Override
    public A update(A aggregateRoot) {
        allAggregates.remove(aggregateRoot);
        allAggregates.add(aggregateRoot);
        return aggregateRoot;
    }

    /**
     * Löscht die AggregateRoot mit der gegebenen ID aus der Liste.
     *
     * @param id, die Identität der zu löschenden AggregateRoot.
     * @return  Optional von der gelöschten AggregateRoot.
     */
    @Override
    public Optional<A> deleteById(ID id) {
        return findById(id).map(a -> {
            allAggregates.remove(a);
            return a;
        });
    }
}