package ar.com.itau.seed.config.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Validated
@Configuration
@ConfigurationProperties("async")
public class AsyncConfig {

    private ExecutorConfig defaultExecutor;

    @Data
    public static class ExecutorConfig {
        @NotNull
        @Positive
        private Integer corePoolSize;
        @NotNull
        @Positive
        private Integer maxPoolSize;
        @NotBlank
        private String threadNamePrefix;
        @NotNull
        private Integer queueCapacity;
    }

}
