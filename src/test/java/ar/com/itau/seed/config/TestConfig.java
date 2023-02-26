package ar.com.itau.seed.config;

import ar.com.itau.seed.adapter.rest.handler.RestTemplateErrorHandler;
import ar.com.itau.seed.config.security.JwtParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@TestConfiguration
@ComponentScan({
        "org.springframework.cloud.sleuth.autoconfig.brave",
        "ar.com.itau.seed.config.async"
})
public class TestConfig {

    /**
     * SCOPE_PROTOTYPE is used so that each adapter instance can customize the error handling.
     *
     * @param restTemplateBuilder Builder
     * @return RestTemplate
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate getRestTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            @Value("${rest.client.connect-timeout}") final int connectTimeout,
            @Value("${rest.client.read-timeout}") final int readTimeout,
            final ObjectMapper objectMapper
    ) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .errorHandler(new RestTemplateErrorHandler(objectMapper))
                .build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Config getConfig() {
        final Config config = new Config();
        config.setPrefix("PRE-FIX:");
        config.setChannelId("I");
        config.setAuthRoleInterceptorEnabled(false);

        final Config.SecurityHeaders securityHeaders = new Config.SecurityHeaders();
        securityHeaders.setAllowedOrigin("*");
        securityHeaders.setAllowedMethods("POST,GET");
        securityHeaders.setAllowedHeaders("Accept,Content-Type,Authorization");
        config.setSecurityHeaders(securityHeaders);

        final Config.SWCharacterRepositoryConfig characterRepositoryConfig = new Config.SWCharacterRepositoryConfig();
        characterRepositoryConfig.setUrl("http://localhost:12345/people/{id}");
        config.setCharacterRepository(characterRepositoryConfig);

        final Config.UserApiRepository userApiRepository = new Config.UserApiRepository();
        userApiRepository.setUrl("http://localhost:4567/users");
        config.setUserRepository(userApiRepository);

        return config;
    }

    @Bean
    public JwtParser jwtParser(final ObjectMapper objectMapper) {
        return new JwtParser(objectMapper);
    }

}
