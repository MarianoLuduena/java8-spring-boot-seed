package ar.com.itau.seed.config;

import ar.com.itau.seed.config.security.KeycloakSpringBootPropertiesConfig;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@TestConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
@Import(KeycloakSpringBootPropertiesConfig.class)
@Order(99)
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().anyRequest().permitAll();
    }

}
