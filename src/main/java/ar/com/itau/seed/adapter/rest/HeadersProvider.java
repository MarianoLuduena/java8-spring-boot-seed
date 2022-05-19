package ar.com.itau.seed.adapter.rest;

import ar.com.itau.seed.config.Config;
import brave.Tracer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HeadersProvider {

    private static final String TRACE_ID_HEADER = "traceId";
    private static final String CHANNEL_ID_HEADER = "channelId";

    private final Config config;
    private final Tracer tracer;

    public HeadersProvider(final Config config, final Tracer tracer) {
        this.config = config;
        this.tracer = tracer;
    }

    public HttpHeaders get() {
        final HttpHeaders headers = new HttpHeaders();
        final String traceId = tracer.currentSpan().context().traceIdString();
        headers.set(TRACE_ID_HEADER, traceId);
        headers.set(CHANNEL_ID_HEADER, config.getChannelId());
        return headers;
    }

}
