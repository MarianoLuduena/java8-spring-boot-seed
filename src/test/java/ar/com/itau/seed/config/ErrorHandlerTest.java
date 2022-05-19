package ar.com.itau.seed.config;

import ar.com.itau.seed.adapter.rest.exception.BadRequestRestClientException;
import ar.com.itau.seed.adapter.rest.exception.RestClientGenericException;
import ar.com.itau.seed.adapter.rest.exception.TimeoutRestClientException;
import ar.com.itau.seed.config.exception.ForbiddenException;
import ar.com.itau.seed.config.exception.NotFoundException;
import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@DisplayName("ErrorHandler Test")
public class ErrorHandlerTest {

    private static final String REQUEST_URL = "/api/v1/resources";
    private static final Long TRACE_ID = 128L;
    private static final Long SPAN_ID = 256L;
    private static final String TIMESTAMP_FIELD = "timestamp";

    private final HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
    private final Tracer tracer = Mockito.mock(Tracer.class);
    private final Config config = new TestConfig().getConfig();

    @BeforeEach
    void setup() {
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URL);
        final Span span = Mockito.mock(Span.class);
        Mockito.when(span.context()).thenReturn(TraceContext.newBuilder().traceId(TRACE_ID).spanId(SPAN_ID).build());
        Mockito.when(tracer.currentSpan()).thenReturn(span);
    }

    @Test
    @DisplayName("a Throwable should be mapped to a 500")
    void testHandleThrowable() {
        final Throwable ex = new Throwable("Some error");
        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleDefault(ex);

        final ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorCode.getReasonPhrase(), errorCode);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("a BadRequestRestClientException should be mapped to a 400")
    void testHandleBadRequestRestClientException() {
        final BadRequestRestClientException ex = new BadRequestRestClientException(ErrorCode.BAD_REQUEST);
        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleBadRequest(ex);

        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ErrorCode.BAD_REQUEST);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("a ConstraintViolationException should be mapped to a 400")
    void testHandleConstraintViolationException() {
        final ConstraintViolationException ex =
                new ConstraintViolationException("X must be positive", Collections.emptySet());
        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleBadRequest(ex);

        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ErrorCode.BAD_REQUEST);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("a ForbiddenException should be mapped to a 403")
    void testHandleForbiddenException() {
        final ForbiddenException ex = new ForbiddenException(ErrorCode.FORBIDDEN);
        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleForbidden(ex);

        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), ErrorCode.FORBIDDEN);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("a NotFoundException should be mapped to a 404")
    void testHandleNotFoundException() {
        final NotFoundException ex = new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND);
        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleNotFound(ex);

        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("a TimeoutRestClientException should be mapped to a 504")
    void testHandleTimeoutRestClientException() {
        final TimeoutRestClientException ex = new TimeoutRestClientException(ErrorCode.INTERNAL_ERROR);
        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleTimeout(ex);

        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.GATEWAY_TIMEOUT, ex.getMessage(), ErrorCode.INTERNAL_ERROR);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("a RestClientGenericException should be mapped to a 500")
    void testHandleRestClientGenericException() {
        final RestClientGenericException ex = new RestClientGenericException(ErrorCode.RESOURCE_NOT_FOUND);
        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleRestClient(ex);

        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("a MissingServletRequestParameterException should be mapped to a 400")
    void testHandleMissingServletRequestParameterException() {
        final MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("aParameterName", "aParameterType");

        final ErrorHandler handler = new ErrorHandler(servletRequest, tracer, config);
        final ResponseEntity<ErrorHandler.ApiErrorResponse> response = handler.handleBadRequest(ex);

        final String expectedMessage = "Bad request, parameter " + ex.getParameterName() + " of type " +
                ex.getParameterType() + " is required";

        final ErrorHandler.ApiErrorResponse expected =
                buildApiErrorResponse(HttpStatus.BAD_REQUEST, expectedMessage, ErrorCode.BAD_REQUEST);

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields(TIMESTAMP_FIELD)
                .isEqualTo(expected);
    }

    private ErrorHandler.ApiErrorResponse buildApiErrorResponse(
            HttpStatus httpStatus,
            String msg,
            ErrorCode errorCode
    ) {
        return ErrorHandler.ApiErrorResponse.builder()
                .timestamp(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                .name(httpStatus.getReasonPhrase())
                .description(msg)
                .status(httpStatus.value())
                .code(config.getPrefix() + errorCode.value())
                .resource(REQUEST_URL)
                .metadata(buildMetadata())
                .build();
    }

    private Map<String, String> buildMetadata() {
        final Map<String, String> metadata = new HashMap<>();
        metadata.put("X-B3-TraceId", String.format("%016x", TRACE_ID));
        metadata.put("X-B3-SpanId", String.format("%016x", SPAN_ID));
        return Collections.unmodifiableMap(metadata);
    }

}
