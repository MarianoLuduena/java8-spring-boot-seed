package ar.com.itau.seed.adapter.rest.handler;

import ar.com.itau.seed.adapter.rest.exception.RestClientGenericException;
import ar.com.itau.seed.config.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.util.Map;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

    private final Map<HttpStatus, RuntimeException> exceptionsMap;

    public RestTemplateErrorHandler(Map<HttpStatus, RuntimeException> exceptionsMap) {
        this.exceptionsMap = exceptionsMap;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        throw exceptionsMap.getOrDefault(response.getStatusCode(),
                new RestClientGenericException(ErrorCode.INTERNAL_ERROR));
    }

}
