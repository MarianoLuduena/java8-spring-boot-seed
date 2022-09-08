package ar.com.itau.seed.config.rest;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

@DisplayName("LogRestTemplateInterceptor Test")
class LogRestTemplateInterceptorTest {

    private static final byte[] BODY = "{ \"foo\": \"bar\" }".getBytes();

    private final HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
    private final ClientHttpRequestExecution execution = Mockito.mock(ClientHttpRequestExecution.class);
    private final ClientHttpResponse httpResponse = Mockito.mock(ClientHttpResponse.class);

    @BeforeEach
    void setup() throws IOException {
        Mockito.reset(httpRequest, execution, httpResponse);
        Mockito.clearAllCaches();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("YWRtaW46MTIzNA==");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Mockito.when(httpRequest.getMethod()).thenReturn(HttpMethod.POST);
        Mockito.when(httpRequest.getURI()).thenReturn(URI.create("http://localhost:1666/api"));
        Mockito.when(httpRequest.getHeaders()).thenReturn(headers);

        Mockito.when(httpResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(httpResponse.getBody()).thenReturn(new BufferedInputStream(new ByteArrayInputStream(BODY)));
        Mockito.when(httpResponse.getHeaders()).thenReturn(headers);

        Mockito.when(execution.execute(Mockito.any(HttpRequest.class), Mockito.any()))
                .thenReturn(httpResponse);
    }

    @AfterEach
    void cleanUp() throws IOException {
        httpResponse.getBody().close();
    }

    @Test
    void testLogInterceptedRestRequest() throws IOException {
        // Given
        final Logger logger = (Logger) LoggerFactory.getLogger(LogRestTemplateInterceptor.class);
        final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        final LogRestTemplateInterceptor interceptor = new LogRestTemplateInterceptor();

        // When
        interceptor.intercept(httpRequest, BODY, execution);

        // Then
        listAppender.stop();

        Assertions.assertThat(listAppender.list.get(0).getFormattedMessage())
                .isEqualTo("POST http://localhost:1666/api | [Authorization=[Basic YWRtaW...], " +
                        "Accept=[application/json]] { \"foo\": \"bar\" }");

        Assertions.assertThat(listAppender.list.get(1).getFormattedMessage())
                .isEqualTo("200 - OK | [Authorization=[Basic YWRtaW...], Accept=[application/json]] | " +
                        "Response: { \"foo\": \"bar\" }");
    }

}
