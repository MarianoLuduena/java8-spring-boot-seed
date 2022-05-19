package ar.com.itau.seed.config.security;

import ar.com.itau.seed.application.port.out.UserRepository;
import ar.com.itau.seed.config.Config;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.TestConfig;
import ar.com.itau.seed.config.exception.ForbiddenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@DisplayName("AuthorizationRoleInterceptor Test")
public class AuthorizationRoleInterceptorTest {

    private static final TestConfig TEST_CONFIG = new TestConfig();
    private static final Config CONFIG = TEST_CONFIG.getConfig();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String ENTERPRISE_ID_HEADER = "enterpriseId";
    private static final String ENTERPRISE_ID = "540492";
    private static final String REQUEST_URI = "/api/v1/characters/4";
    private static final String GET_METHOD = HttpMethod.GET.name();
    private static final String PERMISSION_NAME = "getCharacter";
    private static final String USERNAME = "intiman1";
    private static final String USER_ID = "954";
    private static final String JWT =
            "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3RmxTbnJScVZMUV9ObWo5MTFhZF8taE1pOE1TQ0xrXzVHQXdJ" +
                    "dC05eUNNIn0.eyJqdGkiOiI5MzZmYzFhMi1iMWJhLTQ0NDMtOWU1ZC0wYjE2NjkyODAyZTYiLCJleHAiOjE2NTI5NzM3" +
                    "OTAsIm5iZiI6MCwiaWF0IjoxNjUyOTcxOTkwLCJpc3MiOiJodHRwczovL3Nzby1yaHNzby1pbnRlLmFwcHMub2NwLW5w" +
                    "LnNpcy5hZC5iaWEuaXRhdS9hdXRoL3JlYWxtcy9Ib21lQmFua2luZ0VtcHJlc2FzIiwiYXVkIjoiYWNjb3VudCIsInN1" +
                    "YiI6IjYzOGY3ZTIzLWQxNjYtNDdiYi05Njk2LTc3ZTA5NzBmNGJjMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1lcmN1" +
                    "cnkiLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiJiYzc0NDRkNS00NjE2LTQyYjktYjFkMy1iMmE2NzFiODFi" +
                    "NTAiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxp" +
                    "bmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6" +
                    "eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwi" +
                    "c2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJp" +
                    "bnRpbWFuMSJ9.gq8-YCLa3gDAEKYG93gUBvAle6g58jCB2kXIJeLWPXI6iUpANzUcuPv8qjbOEYh04PpyQwxe696Lnuh" +
                    "spxw9iaDhLA9BeyHhP8osTlyMHBgN5uLwdHia9zOjcFwjx_blPU5jPVKxEVp-YOLXnOEKHKiIRa26UtHDZ-WGagfcZuH" +
                    "EyWSZ8jRzOAYef6LLAylelNeXGta4AcfpazLT8abJtsVu4ylRjdSIkzPj2vSrKBNZvrj0N2kygiFOu2Wju_cfv2vIhRj" +
                    "Rgncyv72xTckTnzp3JLBIlP7X4xj1_1KzlJ7sKbhrFh0SAFLOv6b8kJoI2aCxBLYca40rG2g-exL91w";
    private static final String AUTH_HEADER_VALUE = "Bearer " + JWT;

    private final HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse servletResponse = Mockito.mock(HttpServletResponse.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BeforeAll
    static void setUp() {
        CONFIG.setAuthRoleInterceptorEnabled(true);
    }

    @Test
    @DisplayName("should allow a valid request to continue")
    void testShouldAllowToContinue() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(Mockito.eq(ENTERPRISE_ID_HEADER))).thenReturn(ENTERPRISE_ID);
        Mockito.when(servletRequest.getHeader(Mockito.eq(AUTH_HEADER))).thenReturn(AUTH_HEADER_VALUE);
        Mockito.when(userRepository.getUserIdByUsername(Mockito.eq(USERNAME))).thenReturn(USER_ID);
        Mockito.when(userRepository.hasUserPermission(Mockito.eq(USER_ID), Mockito.eq(PERMISSION_NAME)))
                .thenReturn(true);

        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();

        Mockito.verify(userRepository, Mockito.times(1))
                .hasUserPermission(Mockito.eq(USER_ID), Mockito.eq(PERMISSION_NAME));
    }

    @Test
    @DisplayName("should forbid accessing the resource whenever an error occurs")
    void testForbidAccessIfAnErrorOccurs() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(Mockito.eq(ENTERPRISE_ID_HEADER))).thenReturn(ENTERPRISE_ID);
        Mockito.when(servletRequest.getHeader(Mockito.eq(AUTH_HEADER))).thenReturn(AUTH_HEADER_VALUE);
        Mockito.when(userRepository.getUserIdByUsername(Mockito.eq(USERNAME)))
                .thenThrow(new RuntimeException("Some error"));

        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());

        Mockito.verify(userRepository, Mockito.times(1))
                .getUserIdByUsername(Mockito.eq(USERNAME));
    }

    @Test
    @DisplayName("should reject a request when enterpriseId is missing")
    void testMissingEnterpriseId() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(Mockito.eq(ENTERPRISE_ID_HEADER))).thenReturn(null);
        Mockito.when(servletRequest.getHeader(Mockito.eq(AUTH_HEADER))).thenReturn(AUTH_HEADER_VALUE);

        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());

        Mockito.verify(servletRequest, Mockito.times(1))
                .getHeader(Mockito.eq(ENTERPRISE_ID_HEADER));

        Mockito.verify(servletRequest, Mockito.times(1))
                .getHeader(Mockito.eq(AUTH_HEADER));
    }

    @Test
    @DisplayName("should reject a request when permission name is empty")
    void testEmptyPermissionName() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn("/api/v1/fakes/4");
        Mockito.when(servletRequest.getHeader(Mockito.eq(ENTERPRISE_ID_HEADER))).thenReturn(ENTERPRISE_ID);
        Mockito.when(servletRequest.getHeader(Mockito.eq(AUTH_HEADER))).thenReturn(AUTH_HEADER_VALUE);

        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());

        Mockito.verify(servletRequest, Mockito.times(1))
                .getHeader(Mockito.eq(ENTERPRISE_ID_HEADER));

        Mockito.verify(servletRequest, Mockito.times(1))
                .getHeader(Mockito.eq(AUTH_HEADER));

        Mockito.verify(servletRequest, Mockito.times(1)).getRequestURI();
    }

    @Test
    @DisplayName("should reject a request when the username cannot be extracted from the JWT")
    void testEmptyUsername() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(Mockito.eq(ENTERPRISE_ID_HEADER))).thenReturn(ENTERPRISE_ID);

        final String[] tokens = JWT.split("\\.");
        final String jwt =
                tokens[0]
                        + "." + Base64.getUrlEncoder().encodeToString(new byte[]{0, 1, 2, 3})
                        + "." + tokens[2];
        final String invalidAuthHeader = "Bearer " + jwt;
        Mockito.when(servletRequest.getHeader(Mockito.eq(AUTH_HEADER))).thenReturn(invalidAuthHeader);

        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());
    }

    @Test
    @DisplayName("should not validate anything if it is disabled")
    void testIgnoreValidationIfConfigIsDisabled() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);

        final Config disabledConfig = TEST_CONFIG.getConfig();
        disabledConfig.setAuthRoleInterceptorEnabled(false);

        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(disabledConfig, userRepository, OBJECT_MAPPER);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type ASYNC")
    void testIgnoreAsyncDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.ASYNC);
        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type ERROR")
    void testIgnoreErrorDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.ERROR);
        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type FORWARD")
    void testIgnoreForwardDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.FORWARD);
        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type INCLUDE")
    void testIgnoreIncludeDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.INCLUDE);
        final AuthorizationRoleInterceptor interceptor =
                new AuthorizationRoleInterceptor(CONFIG, userRepository, OBJECT_MAPPER);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

}
