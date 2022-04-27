package ar.com.itau.seed.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomiser customerGlobalHeaderOpenApiCustomizer() {
        return openApi -> {
            final Schema<?> errorSchema =
                    ModelConverters.getInstance()
                            .resolveAsResolvedSchema(new AnnotatedType(ErrorHandler.ApiErrorResponse.class)).schema;
            openApi.schema("Error", errorSchema);

            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                final ApiResponses apiResponses = operation.getResponses();
                Arrays.asList(HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND,
                                HttpStatus.INTERNAL_SERVER_ERROR)
                        .forEach(status -> apiResponses.addApiResponse(
                                        String.valueOf(status.value()),
                                        buildApiResponse(status.getReasonPhrase(), errorSchema)
                                )
                        );
            }));
        };
    }

    private ApiResponse buildApiResponse(final String description, final Schema<?> errorSchema) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(errorSchema)));
    }

}
