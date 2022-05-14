package ar.com.itau.seed.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
@Configuration
@ConfigurationProperties("seed")
public class Config {

    @NotBlank
    private String prefix;
    private SWCharacterRepositoryConfig characterRepository;

    @Data
    public static class SWCharacterRepositoryConfig {
        @NotBlank
        private String url;
    }

}
