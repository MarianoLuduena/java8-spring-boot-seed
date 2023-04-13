package ar.com.itau.seed.config.security;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakSpringBootPropertiesConfig {

    /**
     * keycloakConfigResolver defines that we want to use the Spring Boot properties file support instead of the
     * default keycloak.json
     *
     * @return KeycloakSpringBootConfigResolver
     */
    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

}
