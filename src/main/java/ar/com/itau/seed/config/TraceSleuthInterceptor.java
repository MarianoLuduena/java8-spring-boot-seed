package ar.com.itau.seed.config;

import brave.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class TraceSleuthInterceptor implements AsyncHandlerInterceptor {

    public static final String TRACE_ID_NOT_EXISTS = "Trace id not exists";
    public static final String SPAN_ID_NOT_EXISTS = "Spand id not exists";
    private static final String X_B3_TRACE_ID = "X-B3-TraceId";
    private static final String X_B3_SPAN_ID = "X-B3-SpanId";
    private final Tracer tracer;

    public TraceSleuthInterceptor(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        final String traceId = Optional.ofNullable(this.tracer.currentSpan())
                .map(span -> span.context().traceIdString())
                .orElse(TRACE_ID_NOT_EXISTS);

        if (!response.getHeaderNames().contains(X_B3_TRACE_ID)) {
            response.addHeader(X_B3_TRACE_ID, traceId);
        }

        final String spanId = Optional.ofNullable(this.tracer.currentSpan())
                .map(span -> span.context().spanIdString())
                .orElse(SPAN_ID_NOT_EXISTS);

        if (!response.getHeaderNames().contains(X_B3_SPAN_ID)) {
            response.addHeader(X_B3_SPAN_ID, spanId);
        }

        return true;
    }

}
