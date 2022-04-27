package ar.com.itau.seed.config;

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
@ComponentScan("org.springframework.cloud.sleuth.autoconfig.brave")
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
            RestTemplateBuilder restTemplateBuilder,
            @Value("${rest.client.default.timeout}") int timeout
    ) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Config getConfig() {
        final Config config = new Config();

        final Config.SWCharacterRepositoryConfig characterRepositoryConfig = new Config.SWCharacterRepositoryConfig();
        characterRepositoryConfig.setUrl("http://localhost:12345/people/{id}");

        config.setCharacterRepository(characterRepositoryConfig);
        return config;
    }

}
