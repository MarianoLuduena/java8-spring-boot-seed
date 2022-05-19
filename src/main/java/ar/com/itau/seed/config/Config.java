package ar.com.itau.seed.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("seed")
public class Config {

    @NotBlank
    private String prefix;
    @NotBlank
    private String channelId;
    @NotNull
    private Boolean authRoleInterceptorEnabled;
    private SecurityHeaders securityHeaders;
    private SWCharacterRepositoryConfig characterRepository;
    private UserApiRepository userRepository;

    @Data
    public static class SWCharacterRepositoryConfig {
        @NotBlank
        private String url;
    }

    @Data
    public static class UserApiRepository {
        @NotBlank
        private String url;
    }

    @Data
    public static class SecurityHeaders {
        @NotNull
        private String allowedOrigin;
        @NotNull
        private String allowedMethods;
        @NotNull
        private String allowedHeaders;
    }

}
