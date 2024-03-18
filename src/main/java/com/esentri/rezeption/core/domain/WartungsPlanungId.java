package com.esentri.rezeption.core.domain;

import nitrox.dlc.domain.types.ValueObject;

/**
 * Eine record Klasse die eine WartungsPlanungId eines fremden Systems repräsentiert.
 *
 * @author Mario Herb
 * @see ValueObject
 * @see Record
 */
public record WartungsPlanungId (String id) implements ValueObject {
}
