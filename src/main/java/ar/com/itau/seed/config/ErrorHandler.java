package ar.com.itau.seed.config;

import ar.com.itau.seed.adapter.rest.exception.BadRequestRestClientException;
import ar.com.itau.seed.adapter.rest.exception.RestClientGenericException;
import ar.com.itau.seed.adapter.rest.exception.TimeoutRestClientException;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.config.exception.ValidationException;
import brave.Tracer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    private static final String X_B3_TRACE_ID = "X-B3-TraceId";
    private static final String X_B3_SPAN_ID = "X-B3-SpanId";
    private final HttpServletRequest httpServletRequest;
    private final Tracer tracer;

    public ErrorHandler(final HttpServletRequest httpServletRequest, final Tracer tracer) {
        this.httpServletRequest = httpServletRequest;
        this.tracer = tracer;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiErrorResponse> handleDefault(Throwable ex) {
        log.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex);
        final ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        return buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, errorCode.getReasonPhrase(), errorCode);
    }

    @ExceptionHandler(BadRequestRestClientException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestRestClientException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        return buildResponseError(HttpStatus.BAD_REQUEST, ex, ex.getCode());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(ConstraintViolationException ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        return buildResponseError(HttpStatus.BAD_REQUEST, ex, ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        return buildBadRequestResponseError(
                () -> "parameter " + ex.getName() + " must be of type " +
                        ex.getParameter().getParameterType().getSimpleName(),
                ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex) {
        log.error(HttpStatus.NOT_FOUND.getReasonPhrase(), ex);
        return buildResponseError(HttpStatus.NOT_FOUND, ex, ex.getCode());
    }

    @ExceptionHandler(TimeoutRestClientException.class)
    public ResponseEntity<ApiErrorResponse> handleTimeout(TimeoutRestClientException ex) {
        log.error(HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(), ex);
        return buildResponseError(HttpStatus.GATEWAY_TIMEOUT, ex, ex.getCode());
    }

    @ExceptionHandler(RestClientGenericException.class)
    public ResponseEntity<ApiErrorResponse> handleRestClient(RestClientGenericException ex) {
        log.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex);
        return buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex, ex.getCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(MissingServletRequestParameterException ex) {
        return buildBadRequestResponseError(
                () -> "parameter " + ex.getParameterName() + " of type " + ex.getParameterType() + " is required",
                ex
        );
    }

    private ResponseEntity<ApiErrorResponse> buildBadRequestResponseError(Supplier<String> messageFn, Throwable ex) {
        log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
        final String message = messageFn.get();
        final ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        final ValidationException wrappingException = new ValidationException(errorCode, message, ex);
        return buildResponseError(HttpStatus.BAD_REQUEST, wrappingException, errorCode);
    }

    private ResponseEntity<ApiErrorResponse> buildResponseError(
            final HttpStatus httpStatus,
            final Throwable ex,
            final ErrorCode errorCode
    ) {
        return buildResponseError(httpStatus, ex.getMessage(), errorCode);
    }

    private ResponseEntity<ApiErrorResponse> buildResponseError(
            final HttpStatus httpStatus,
            final String message,
            final ErrorCode errorCode
    ) {

        final String traceId = Optional.ofNullable(this.tracer.currentSpan())
                .map(span -> span.context().traceIdString())
                .orElse(TraceSleuthInterceptor.TRACE_ID_NOT_EXISTS);

        final String spanId = Optional.ofNullable(this.tracer.currentSpan())
                .map(span -> span.context().spanIdString())
                .orElse(TraceSleuthInterceptor.SPAN_ID_NOT_EXISTS);

        final Map<String, String> metadata = new HashMap<>();
        metadata.put(X_B3_TRACE_ID, traceId);
        metadata.put(X_B3_SPAN_ID, spanId);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .timestamp(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                .name(httpStatus.getReasonPhrase())
                .detail(message)
                .status(httpStatus.value())
                .code(errorCode.value())
                .resource(httpServletRequest.getRequestURI())
                .metadata(Collections.unmodifiableMap(metadata))
                .build();

        return new ResponseEntity<>(apiErrorResponse, httpStatus);
    }

    @Builder
    @NonNull
    @lombok.Value
    public static class ApiErrorResponse {
        private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS]X";

        String name;
        Integer status;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
        ZonedDateTime timestamp;
        Integer code;
        String resource;
        String detail;
        Map<String, String> metadata;
    }

}