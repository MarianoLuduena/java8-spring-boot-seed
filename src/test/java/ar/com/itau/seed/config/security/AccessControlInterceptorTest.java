package ar.com.itau.seed.config.security;

import ar.com.itau.seed.config.Config;
import ar.com.itau.seed.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@DisplayName("AccessControlInterceptor Test")
class AccessControlInterceptorTest {

    private final HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse servletResponse = Mockito.mock(HttpServletResponse.class);
    private final TestConfig testConfig = new TestConfig();
    private final Config config = testConfig.getConfig();

    @Test
    @DisplayName("a response should be completed with all access control headers if defined")
    void testSettingAccessControlHeaders() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);

        final AccessControlInterceptor interceptor = new AccessControlInterceptor(config);
        interceptor.preHandle(servletRequest, servletResponse, "");

        final Config.SecurityHeaders expected = config.getSecurityHeaders();
        Mockito.verify(servletResponse, Mockito.times(1))
                .setHeader(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        expected.getAllowedOrigin()
                );

        Mockito.verify(servletResponse, Mockito.times(1))
                .setHeader(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        expected.getAllowedMethods()
                );

        Mockito.verify(servletResponse, Mockito.times(1))
                .setHeader(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        expected.getAllowedHeaders()
                );
    }

    @Test
    @DisplayName("a response should not be completed at all if request is ASYNC")
    void testNotSettingHeadersForAsyncDispatch() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.ASYNC);

        final AccessControlInterceptor interceptor = new AccessControlInterceptor(config);
        interceptor.preHandle(servletRequest, servletResponse, "");
    }

    @Test
    @DisplayName("a response should not be completed with empty headers")
    void testNotSettingHeadersIfTheyAreEmpty() {
        final Config emptyConfig = testConfig.getConfig();
        emptyConfig.getSecurityHeaders().setAllowedMethods("");
        emptyConfig.getSecurityHeaders().setAllowedHeaders("");
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);

        final AccessControlInterceptor interceptor = new AccessControlInterceptor(emptyConfig);
        interceptor.preHandle(servletRequest, servletResponse, "");

        Mockito.verify(servletResponse, Mockito.times(1))
                .setHeader(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        emptyConfig.getSecurityHeaders().getAllowedOrigin()
                );
    }

}
