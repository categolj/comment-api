package am.ik.blog;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

@TestConfiguration
public class MockConfig {

	@Bean
	@Primary
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Clock incrementalClock() {
		Instant base = Instant.parse("2024-11-01T00:00:00Z");
		AtomicInteger counter = new AtomicInteger(0);
		return new Clock() {

			@Override
			public ZoneId getZone() {
				return ZoneId.of("UTC");
			}

			@Override
			public Clock withZone(ZoneId zone) {
				return this; // ignore
			}

			@Override
			public Instant instant() {
				return base.plusSeconds(counter.getAndIncrement());
			}
		};
	}

}
