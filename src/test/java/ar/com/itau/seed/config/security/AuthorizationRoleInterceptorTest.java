package ar.com.itau.seed.config.security;

import ar.com.itau.seed.application.port.out.UserRepository;
import ar.com.itau.seed.config.Config;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.TestConfig;
import ar.com.itau.seed.config.exception.ForbiddenException;
import ar.com.itau.seed.mock.AuthenticationMockFactory;
import ar.com.itau.seed.mock.UserMockFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
class AuthorizationRoleInterceptorTest {

    private static final TestConfig TEST_CONFIG = new TestConfig();
    private static final Config CONFIG = TEST_CONFIG.getConfig();
    private static final JwtParser JWT_PARSER = new JwtParser(new ObjectMapper());
    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String ENTERPRISE_ID_HEADER = "enterpriseId";
    private static final String ENTERPRISE_ID = "540492";
    private static final String REQUEST_URI = "/api/v1/characters/4";
    private static final String WHITELISTED_URI = "/actuator/health";
    private static final String GET_METHOD = HttpMethod.GET.name();
    private static final String PERMISSION_NAME = "getCharacter";
    private static final String USERNAME = AuthenticationMockFactory.getPreferredUsername();
    private static final String USER_ID = "954";
    private static final String JWT = AuthenticationMockFactory.getExpiredJwt();

    private static final String AUTH_HEADER_VALUE = "Bearer " + JWT;

    private final HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse servletResponse = Mockito.mock(HttpServletResponse.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private AuthorizationRoleInterceptor interceptor;

    @BeforeAll
    static void setUp() {
        CONFIG.setAuthRoleInterceptorEnabled(true);
    }

    @BeforeEach
    void beforeEach() {
        interceptor = new AuthorizationRoleInterceptor(CONFIG, userRepository, JWT_PARSER);
    }

    @Test
    @DisplayName("should allow a valid request to continue")
    void testShouldAllowToContinue() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(ENTERPRISE_ID_HEADER)).thenReturn(ENTERPRISE_ID);
        Mockito.when(servletRequest.getHeader(AUTH_HEADER)).thenReturn(AUTH_HEADER_VALUE);
        Mockito.when(userRepository.getUserByUsername(USERNAME)).thenReturn(UserMockFactory.user());
        Mockito.when(userRepository.hasUserPermission(USER_ID, PERMISSION_NAME))
                .thenReturn(true);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();

        Mockito.verify(userRepository, Mockito.times(1))
                .hasUserPermission(USER_ID, PERMISSION_NAME);
    }

    @Test
    @DisplayName("should forbid accessing the resource whenever an error occurs")
    void testForbidAccessIfAnErrorOccurs() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(ENTERPRISE_ID_HEADER)).thenReturn(ENTERPRISE_ID);
        Mockito.when(servletRequest.getHeader(AUTH_HEADER)).thenReturn(AUTH_HEADER_VALUE);
        Mockito.when(userRepository.getUserByUsername(USERNAME))
                .thenThrow(new RuntimeException("Some error"));

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());

        Mockito.verify(userRepository, Mockito.times(1))
                .getUserByUsername(USERNAME);
    }

    @Test
    @DisplayName("should reject a request when enterpriseId is missing")
    void testMissingEnterpriseId() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(ENTERPRISE_ID_HEADER)).thenReturn(null);
        Mockito.when(servletRequest.getHeader(AUTH_HEADER)).thenReturn(AUTH_HEADER_VALUE);

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());

        Mockito.verify(servletRequest, Mockito.times(1)).getHeader(ENTERPRISE_ID_HEADER);
        Mockito.verify(servletRequest, Mockito.times(1)).getHeader(AUTH_HEADER);
    }

    @Test
    @DisplayName("should reject a request when permission name is empty")
    void testEmptyPermissionName() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn("/api/v1/fakes/4");
        Mockito.when(servletRequest.getHeader(ENTERPRISE_ID_HEADER)).thenReturn(ENTERPRISE_ID);
        Mockito.when(servletRequest.getHeader(AUTH_HEADER)).thenReturn(AUTH_HEADER_VALUE);

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());

        Mockito.verify(servletRequest, Mockito.times(1)).getHeader(ENTERPRISE_ID_HEADER);
        Mockito.verify(servletRequest, Mockito.times(1)).getHeader(AUTH_HEADER);
        Mockito.verify(servletRequest, Mockito.times(1)).getRequestURI();
    }

    @Test
    @DisplayName("should reject a request when the username cannot be extracted from the JWT")
    void testEmptyUsername() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(ENTERPRISE_ID_HEADER)).thenReturn(ENTERPRISE_ID);

        final String[] tokens = JWT.split("\\.");
        final String jwt =
                tokens[0]
                        + "." + Base64.getUrlEncoder().encodeToString(new byte[]{0, 1, 2, 3})
                        + "." + tokens[2];
        final String invalidAuthHeader = "Bearer " + jwt;
        Mockito.when(servletRequest.getHeader(AUTH_HEADER)).thenReturn(invalidAuthHeader);

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
                new AuthorizationRoleInterceptor(disabledConfig, userRepository, JWT_PARSER);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type ASYNC")
    void testIgnoreAsyncDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.ASYNC);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type ERROR")
    void testIgnoreErrorDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.ERROR);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type FORWARD")
    void testIgnoreForwardDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.FORWARD);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should not process dispatches of type INCLUDE")
    void testIgnoreIncludeDispatchType() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.INCLUDE);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("should allow a whitelisted request to continue")
    void testShouldAllowToContinueIfUriIsWhitelisted() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(WHITELISTED_URI);

        final boolean actual = interceptor.preHandle(servletRequest, servletResponse, "");
        Assertions.assertThat(actual).isTrue();

        Mockito.verify(userRepository, Mockito.never()).hasUserPermission(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    @DisplayName("should reject the request if the user has nothing to do with the company (enterprise)")
    void testUserHasNoAccessToCompany() {
        Mockito.when(servletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        Mockito.when(servletRequest.getMethod()).thenReturn(GET_METHOD);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        Mockito.when(servletRequest.getHeader(ENTERPRISE_ID_HEADER)).thenReturn(ENTERPRISE_ID);
        Mockito.when(servletRequest.getHeader(AUTH_HEADER)).thenReturn(AUTH_HEADER_VALUE);
        Mockito.when(userRepository.getUserByUsername(USERNAME)).thenReturn(UserMockFactory.inactiveUser());

        final Throwable thrown = Assertions.catchThrowable(
                () -> interceptor.preHandle(servletRequest, servletResponse, "")
        );

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getReasonPhrase());
    }

}
