package am.ik.blog;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	}

	@Bean
	GenericContainer<?> authorizationServer() {
		return new GenericContainer<>("ghcr.io/making/oauth2-sso-demo/authorization:jvm")
			.withEnv("spring.security.user.name", "test@example.com")
			.withEnv("spring.security.user.password", "test")
			.withEnv("spring.security.oauth2.authorizationserver.client.google.registration.authorization-grant-types",
					"authorization_code,refresh_token")
			.withEnv(
					"spring.security.oauth2.authorizationserver.client.google.registration.client-authentication-methods",
					"client_secret_basic")
			.withEnv("spring.security.oauth2.authorizationserver.client.google.registration.client-id", "google")
			.withEnv("spring.security.oauth2.authorizationserver.client.google.registration.client-secret",
					"{noop}google")
			.withEnv("spring.security.oauth2.authorizationserver.client.google.registration.post-logout-redirect-uris",
					"http://localhost:8080")
			.withEnv("spring.security.oauth2.authorizationserver.client.google.registration.redirect-uris",
					"http://localhost:8080/login/oauth2/code/google")
			.withEnv("spring.security.oauth2.authorizationserver.client.google.registration.scopes",
					"openid,email,profile")
			.withEnv("spring.security.oauth2.authorizationserver.client.google.require-authorization-consent", "false")
			.withEnv("management.zipkin.tracing.export.enabled", "false")
			.withEnv("spring.main.banner-mode", "off")
			.withExposedPorts(9000)
			.waitingFor(Wait.forHttp("/actuator/health").forPort(9000).withStartupTimeout(Duration.ofSeconds(10)))
			.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("authorization-server")));
	}

	@Bean
	DynamicPropertyRegistrar dynamicPropertyRegistrar(GenericContainer<?> authorizationServer) {
		return registry -> registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> "http://127.0.0.1:" + authorizationServer.getMappedPort(9000));
	}

}
