package ar.com.itau.seed.config.security;

import ar.com.itau.seed.config.Config;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AccessControlInterceptor implements AsyncHandlerInterceptor {

    private final Config config;

    public AccessControlInterceptor(final Config config) {
        this.config = config;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        if (request.getDispatcherType() != DispatcherType.ASYNC) {
            final Config.SecurityHeaders headers = config.getSecurityHeaders();
            setHeaderIfNotEmpty(response, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, headers.getAllowedOrigin());
            setHeaderIfNotEmpty(response, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, headers.getAllowedMethods());
            setHeaderIfNotEmpty(response, HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, headers.getAllowedHeaders());
        }

        return true;
    }

    private void setHeaderIfNotEmpty(
            final HttpServletResponse response,
            final String header,
            final String value
    ) {
        if (!value.trim().equals("")) {
            response.setHeader(header, value);
        }
    }

}
