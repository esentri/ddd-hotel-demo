package com.esentri.hotel.setupcheck.core.domain;

import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.AggregateRootBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

public class SetupCheck extends AggregateRootBase<SetupCheck.SetupCheckId> {

    public record SetupCheckId(@NotNull UUID value) implements Identity<UUID> {
    }

    @NotNull
    private final SetupCheckId id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Builder
    public SetupCheck(long concurrencyVersion, SetupCheckId id, String name) {
        super(concurrencyVersion);
        this.id = id;
        this.name = name;
    }

    public static SetupCheck create(String name) {
        return new SetupCheck(0, new SetupCheckId(UUID.randomUUID()), name);
    }

    public SetupCheck rename(String newName) {
        this.name = newName;
        return this;
    }

    public String getName() {
        return name;
    }
}
