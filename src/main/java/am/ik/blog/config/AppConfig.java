package am.ik.blog.config;

import java.time.Clock;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	public RestTemplateCustomizer restTemplateCustomizer(
			LogbookClientHttpRequestInterceptor logbookClientHttpRequestInterceptor) {
		return restTemplate -> restTemplate.getInterceptors().addFirst(logbookClientHttpRequestInterceptor);
	}

	@Bean
	public RestClientCustomizer restClientCustomizer(
			LogbookClientHttpRequestInterceptor logbookClientHttpRequestInterceptor) {
		return restClient -> restClient.requestInterceptor(logbookClientHttpRequestInterceptor);
	}

}