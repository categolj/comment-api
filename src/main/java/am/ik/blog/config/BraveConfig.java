package am.ik.blog.config;

import org.springframework.boot.actuate.autoconfigure.opentelemetry.OpenTelemetryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.Encoding;
import zipkin2.reporter.otel.brave.OtlpProtoV1Encoder;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpenTelemetryProperties.class)
public class BraveConfig {

	@Bean
	public OtlpProtoV1Encoder otlpProtoV1Encoder(OpenTelemetryProperties properties) {
		return OtlpProtoV1Encoder.newBuilder().resourceAttributes(properties.getResourceAttributes()).build();
	}

	@Bean
	public Encoding otlpEncoding() {
		return Encoding.PROTO3;
	}

}