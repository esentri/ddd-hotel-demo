package com.esentri.hotel.rezeption.core.domain.hotel;

import io.domainlifecycles.domain.types.Identity;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Referenz auf ein Hotel. Die Software laeuft fuer mehrere Hotels; Buchungen und Zimmer gehoeren immer zu genau
 * einem Hotel.
 */
public record HotelId(@NotNull UUID value) implements Identity<UUID> {
}
