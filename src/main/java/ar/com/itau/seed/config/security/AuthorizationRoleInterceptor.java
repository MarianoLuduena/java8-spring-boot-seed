package ar.com.itau.seed.config.security;

import ar.com.itau.seed.application.port.out.UserRepository;
import ar.com.itau.seed.config.Config;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.ForbiddenException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Optional;
import java.util.Base64;
import java.util.regex.Pattern;

@Slf4j
@Component
public class AuthorizationRoleInterceptor implements AsyncHandlerInterceptor {

    private static final String BEARER_AUTHORIZATION_TYPE = "Bearer ";
    private static final String JWT_SECTION_DELIMITER = "\\.";
    private static final String ENTERPRISE_ID_HEADER = "enterpriseId";
    private static final String GET_CHARACTER_BY_ID_URL_PATTERN = "^/api/v1/characters/\\d+$";
    private static final String GET_CHARACTER_BY_ID_PERMISSION = "getCharacter";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";

    private final Config config;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final Map<String, List<UriOperationPair>> endpointsByMethod;

    public AuthorizationRoleInterceptor(
            final Config config,
            final UserRepository userRepository,
            final ObjectMapper objectMapper
    ) {
        this.config = config;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.endpointsByMethod = new HashMap<>();

        this.endpointsByMethod.put(
                HttpMethod.GET.name(),
                Collections.singletonList(new UriOperationPair(Pattern.compile(GET_CHARACTER_BY_ID_URL_PATTERN),
                        GET_CHARACTER_BY_ID_PERMISSION))
        );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getDispatcherType() != DispatcherType.REQUEST) return true;
        if (!config.getAuthRoleInterceptorEnabled()) return true;

        if (!canOperationBeExecuted(request)) throw new ForbiddenException(ErrorCode.FORBIDDEN);

        return true;
    }

    private boolean canOperationBeExecuted(HttpServletRequest request) {
        try {
            final String permissionName = getPermissionNameForOperation(request.getMethod(), request.getRequestURI());
            final String enterpriseId = Optional.ofNullable(request.getHeader(ENTERPRISE_ID_HEADER)).orElse("");
            final String username = getUsernameFromRequest(request);
            return isPermissionValidForUser(username, permissionName, enterpriseId);
        } catch (Throwable ex) {
            log.error("Cannot grant access to {} {}", request.getMethod(), request.getRequestURI(), ex);
            return false;
        }
    }

    private String getPermissionNameForOperation(final String method, final String uri) {
        for (UriOperationPair pair : endpointsByMethod.getOrDefault(method, Collections.emptyList())) {
            if (pair.matches(uri)) return pair.operation;
        }
        return "";
    }

    private String getUsernameFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(authorization -> authorization.startsWith(BEARER_AUTHORIZATION_TYPE))
                .map(authorization -> authorization.replace(BEARER_AUTHORIZATION_TYPE, ""))
                .map(this::extractUsernameFromJwt)
                .orElse("");
    }

    private String extractUsernameFromJwt(final String jwt) {
        try {
            final String encodedPayload = jwt.split(JWT_SECTION_DELIMITER)[1];
            final JsonNode json = objectMapper.readTree(Base64.getUrlDecoder().decode(encodedPayload));
            return json.get(PREFERRED_USERNAME_CLAIM).asText("");
        } catch (Throwable ex) {
            log.error("Could not get username from JWT", ex);
            return "";
        }
    }

    private boolean isPermissionValidForUser(
            final String username,
            final String permissionName,
            final String enterpriseId
    ) {
        if (username.isEmpty() || permissionName.isEmpty() || enterpriseId.isEmpty()) return false;
        final String userId = userRepository.getUserIdByUsername(username);
        return userRepository.hasUserPermission(userId, permissionName);
    }

    @Value
    private static class UriOperationPair {
        Pattern uriPattern;
        String operation;

        public boolean matches(String uri) {
            return uriPattern.matcher(uri).matches();
        }
    }

}
