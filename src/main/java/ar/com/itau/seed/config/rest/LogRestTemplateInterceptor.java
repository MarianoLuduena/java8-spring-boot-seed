package ar.com.itau.seed.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LogRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_VALUE_SUFFIX = "...";
    private static final int MAX_REQUEST_BODY_LENGTH_TO_LOG = 4 * 1024;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        final String bodyToLog =
                body.length > MAX_REQUEST_BODY_LENGTH_TO_LOG
                        ? "[" + body.length + " bytes]"
                        : new String(body, StandardCharsets.UTF_8);

        log.info(
                "{} {} | {} {}",
                request.getMethod(),
                request.getURI(),
                concealSensitiveData(request.getHeaders()),
                bodyToLog
        );
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        final HttpStatus httpStatus = response.getStatusCode();
        final HttpHeaders headers = response.getHeaders();
        final String body =
                MediaType.APPLICATION_OCTET_STREAM.equals(headers.getContentType())
                        ? "[" + headers.getContentLength() + " bytes]"
                        : StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);

        log.info(
                "{} - {} | {} | Response: {}",
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                concealSensitiveData(headers),
                body
        );
    }

    private List<Map.Entry<String, List<String>>> concealSensitiveData(HttpHeaders headers) {
        return headers.entrySet().stream().map(entry -> {
            if (AUTHORIZATION_HEADER.equalsIgnoreCase(entry.getKey())) {
                final String value = entry.getValue().get(0);
                return new AbstractMap.SimpleImmutableEntry<>(
                        entry.getKey(),
                        Collections.singletonList(
                                value.substring(0, Math.min(value.length(), 12)) + AUTHORIZATION_VALUE_SUFFIX
                        )
                );
            }
            return entry;
        }).collect(Collectors.toList());
    }

}
