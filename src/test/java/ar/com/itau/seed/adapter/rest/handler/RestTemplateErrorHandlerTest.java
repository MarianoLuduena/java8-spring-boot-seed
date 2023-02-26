package ar.com.itau.seed.adapter.rest.handler;

import ar.com.itau.seed.adapter.rest.exception.BadRequestRestClientException;
import ar.com.itau.seed.adapter.rest.exception.RestClientGenericException;
import ar.com.itau.seed.adapter.rest.exception.TimeoutRestClientException;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.helper.JsonHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@DisplayName("RestTemplateErrorHandler Test")
class RestTemplateErrorHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DESCRIPTION = "some description";
    private static final String ERROR_MESSAGE = "Internal server error";

    private final ClientHttpResponse clientHttpResponse = Mockito.mock(ClientHttpResponse.class);

    private InputStream inputStream;

    private RestTemplateErrorHandler errorHandler;

    @BeforeEach
    void before() {
        errorHandler = new RestTemplateErrorHandler(OBJECT_MAPPER);
    }

    @AfterEach
    void setUp() throws IOException {
        if (inputStream != null) inputStream.close();
    }

    @Test
    @DisplayName("hasError should be true if the HTTP Status is erroneous")
    void hasErrorShouldBeTrueIfStatusIsError() throws IOException {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(status);
        Assertions.assertThat(errorHandler.hasError(clientHttpResponse)).isTrue();
        Assertions.assertThat(status.isError()).isTrue();
    }

    @Test
    @DisplayName("hasError should be false if the HTTP Status is successful")
    void hasErrorShouldBeFalseIfStatusIsOk() throws IOException {
        final HttpStatus status = HttpStatus.OK;
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        Assertions.assertThat(errorHandler.hasError(clientHttpResponse)).isFalse();
        Assertions.assertThat(status.isError()).isFalse();
    }

    @Test
    @DisplayName("should handle a Bad Request without a body")
    void testHandleBadRequestWithoutBody() throws IOException {
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        inputStream = new ByteArrayInputStream("".getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(BadRequestRestClientException.class)
                .hasMessage(ERROR_MESSAGE);
    }

    @Test
    @DisplayName("should handle a Bad Request with a body missing the required fields")
    void testHandleBadRequestWithBodyMissingTheRequiredFields() throws IOException {
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        inputStream = new ByteArrayInputStream("{}".getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(BadRequestRestClientException.class)
                .hasMessage("");
    }

    @Test
    @DisplayName("should handle a specific Bad Request error (API-ACC:400:401)")
    void testHandleSpecificBadRequestCode() throws IOException {
        final String body = JsonHelper.bodyErrorCode("API-ACC:400:401", ErrorCode.RESOURCE_NOT_FOUND.getReasonPhrase());
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        inputStream = new ByteArrayInputStream(body.getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(BadRequestRestClientException.class)
                .hasMessage(ErrorCode.RESOURCE_NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("should handle a Not Found without a body")
    void testHandleNotFoundWithoutBody() throws IOException {
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        inputStream = new ByteArrayInputStream("".getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(ERROR_MESSAGE);
    }

    @Test
    @DisplayName("should handle a specific Not Found error (API-SEE:104)")
    void testHandleSpecificNotFoundCode() throws IOException {
        final String body = JsonHelper.bodyErrorCode("API-SEE:104", ErrorCode.RESOURCE_NOT_FOUND.getReasonPhrase());
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        inputStream = new ByteArrayInputStream(body.getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.RESOURCE_NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("should handle a Gateway Timeout without a body")
    void testHandleGatewayTimeoutWithoutBody() throws IOException {
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.GATEWAY_TIMEOUT);

        inputStream = new ByteArrayInputStream("".getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(TimeoutRestClientException.class)
                .hasMessage(ERROR_MESSAGE);
    }

    @Test
    @DisplayName("should handle an unknown HttpStatus as an Internal Server Error")
    void testHandleUnknownHttpStatus() throws IOException {
        final String body = JsonHelper.bodyErrorCode("unknown", ErrorCode.INTERNAL_ERROR.getReasonPhrase());
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        inputStream = new ByteArrayInputStream(body.getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(RestClientGenericException.class)
                .hasMessage(ErrorCode.INTERNAL_ERROR.getReasonPhrase());
    }

    @Test
    @DisplayName("should handle a specific Bad Request external error (EXT-SEE:P33023)")
    void testExternalErrorBadRequestCode() throws IOException {
        final String body = JsonHelper.bodyErrorCode("EXT-SEE:P33023", DESCRIPTION);
        Mockito.when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        inputStream = new ByteArrayInputStream(body.getBytes());
        Mockito.when(clientHttpResponse.getBody()).thenReturn(inputStream);

        final Throwable thrown = Assertions.catchThrowable(() -> errorHandler.handleError(clientHttpResponse));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(BadRequestRestClientException.class)
                .hasMessage(DESCRIPTION);
    }

}
