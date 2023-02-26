package ar.com.itau.seed.adapter.rest.handler;

import ar.com.itau.seed.adapter.rest.exception.BadRequestRestClientException;
import ar.com.itau.seed.adapter.rest.exception.RestClientGenericException;
import ar.com.itau.seed.adapter.rest.exception.TimeoutRestClientException;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.NotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    private static final Map<HttpStatus, Function<ErrorCode, RuntimeException>> DEFAULT_EXCEPTIONS_BY_HTTP_STATUS_CODE =
            Optional.of(new EnumMap<HttpStatus, Function<ErrorCode, RuntimeException>>(HttpStatus.class))
                    .map(m -> {
                        m.put(HttpStatus.BAD_REQUEST, BadRequestRestClientException::new);
                        m.put(HttpStatus.NOT_FOUND, NotFoundException::new);
                        m.put(HttpStatus.GATEWAY_TIMEOUT, TimeoutRestClientException::new);
                        return Collections.unmodifiableMap(m);
                    }).get();

    private final ObjectMapper objectMapper;

    public RestTemplateErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        final HttpStatus httpStatus = response.getStatusCode();
        final ErrorBody body = parseResponseBody(response);
        final String code = emptyIfNull(body.getCode());
        final String description = emptyIfNull(body.getDescription());
        final ErrorCode errorCode = new ErrorCode(code, description);
        throw Optional.ofNullable(DEFAULT_EXCEPTIONS_BY_HTTP_STATUS_CODE.get(httpStatus))
                .map(strategy -> strategy.apply(errorCode))
                .orElseGet(() -> new RestClientGenericException(errorCode));
    }

    private ErrorBody parseResponseBody(final ClientHttpResponse response) {
        try {
            return objectMapper.readValue(response.getBody(), ErrorBody.class);
        } catch (IOException e) {
            log.warn("Error while reading response body. An empty code is returned", e);
            final ErrorBody body = new ErrorBody();
            body.setCode(ErrorCode.INTERNAL_ERROR.value());
            body.setDescription(ErrorCode.INTERNAL_ERROR.getReasonPhrase());
            return body;
        }
    }

    private String emptyIfNull(String value) {
        return Objects.nonNull(value) ? value : "";
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ErrorBody {
        private String code;
        private String description;
    }

}
