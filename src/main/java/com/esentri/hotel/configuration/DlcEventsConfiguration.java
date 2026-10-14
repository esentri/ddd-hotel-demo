package com.esentri.hotel.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * DLC registers every {@code @DomainEventListener} of a service as a Spring {@code @TransactionalEventListener}
 * (after commit, {@code REQUIRES_NEW}) annotated with {@code @Async}. {@code @Async} only takes effect with
 * {@code @EnableAsync}: without it, listeners would run synchronously in the publisher's thread and a listener
 * failure would surface at the caller although its transaction is already committed.
 */
@Configuration
@EnableAsync
public class DlcEventsConfiguration {
}
