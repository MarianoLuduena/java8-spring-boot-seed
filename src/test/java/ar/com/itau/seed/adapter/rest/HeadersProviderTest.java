package ar.com.itau.seed.adapter.rest;

import ar.com.itau.seed.config.Config;
import ar.com.itau.seed.config.TestConfig;
import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import java.util.Collections;

@DisplayName("HeadersProvider Test")
class HeadersProviderTest {

    private static final Config CONFIG = new TestConfig().getConfig();
    private static final String TRACE_ID_HEADER = "traceId";
    private static final String CHANNEL_ID_HEADER = "channelId";
    private static final String TRACE_ID = "000004a817c8f600";

    @Test
    @DisplayName("when get is called it should return the default headers")
    void testGetDefaultHeaders() {
        final Tracer tracer = Mockito.mock(Tracer.class);
        final Span span = Mockito.mock(Span.class);
        final TraceContext context =
                TraceContext.newBuilder()
                        .traceId(Long.parseLong(TRACE_ID, 16))
                        .spanId(Long.parseLong(TRACE_ID, 16))
                        .build();

        Mockito.when(tracer.currentSpan()).thenReturn(span);
        Mockito.when(span.context()).thenReturn(context);

        final HeadersProvider provider = new HeadersProvider(CONFIG, tracer);
        final HttpHeaders actual = provider.get();

        Assertions.assertThat(actual.getOrEmpty(TRACE_ID_HEADER)).isEqualTo(Collections.singletonList(TRACE_ID));

        Assertions.assertThat(actual.getOrEmpty(CHANNEL_ID_HEADER))
                .isEqualTo(Collections.singletonList(CONFIG.getChannelId()));
    }

}
