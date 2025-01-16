package am.ik.blog.config;

import io.opentelemetry.proto.trace.v1.Span;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.autoconfigure.opentelemetry.OpenTelemetryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.Encoding;
import zipkin2.reporter.otel.brave.InstrumentationScope;
import zipkin2.reporter.otel.brave.OtlpProtoV1Encoder;
import zipkin2.reporter.otel.brave.TagToAttribute;
import zipkin2.reporter.otel.brave.TagToAttributes;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpenTelemetryProperties.class)
public class BraveConfig {

	@Bean
	public OtlpProtoV1Encoder otlpProtoV1Encoder(OpenTelemetryProperties properties) {
		return OtlpProtoV1Encoder.newBuilder()
			.instrumentationScope(new InstrumentationScope("org.springframework.boot", SpringBootVersion.getVersion()))
			.resourceAttributes(properties.getResourceAttributes())
			.tagToAttributes(TagToAttributes.newBuilder()
				.withDefaults()
				.tagToAttribute("method", "http.request.method")
				.tagToAttribute("uri", (builder, value) -> {
					Span span = builder.build();
					if (span.getKind() == Span.SpanKind.SPAN_KIND_SERVER) {
						builder.addAttributes(TagToAttribute.stringAttribute("http.route", value));
					}
					else if (span.getKind() == Span.SpanKind.SPAN_KIND_CLIENT) {
						builder.addAttributes(TagToAttribute.stringAttribute("url.template", value));
					}
					else {
						builder.addAttributes(TagToAttribute.stringAttribute("uri", value));
					}
				})
				.tagToAttribute("jdbc.query[0]", "db.query.text")
				.tagToAttribute("jdbc.row-count", "db.response.returned_rows")
				.build())
			.build();
	}

	@Bean
	public Encoding otlpEncoding() {
		return Encoding.PROTO3;
	}

}
