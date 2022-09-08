package ar.com.itau.seed.adapter.rest;

import ar.com.itau.seed.adapter.rest.model.user.UserPermissionCheckRestModel;
import ar.com.itau.seed.adapter.rest.model.user.UserRestModel;
import ar.com.itau.seed.adapter.rest.model.user.UserSearchRestModel;
import ar.com.itau.seed.application.port.out.UserRepository;
import ar.com.itau.seed.config.Config;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.domain.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Repository
public class UserRestAdapter implements UserRepository {

    private static final String USERS_RESOURCE = "/users";
    private static final String PERMISSIONS_RESOURCE = "/permissions";
    private static final String PERMISSION_NAME_RESOURCE = "/name";
    private static final String USERNAME_QUERY_PARAM = "userName=";
    private static final String AUDIT_USER_HEADER = "auditUser";
    private static final String QUERY_STRING_DELIMITER = "?";

    private final RestTemplate restTemplate;
    private final Config config;
    private final HeadersProvider headersProvider;

    public UserRestAdapter(
            final RestTemplate restTemplate,
            final Config config,
            final HeadersProvider headersProvider
    ) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.headersProvider = headersProvider;
    }

    public User getUserByUsername(final String username) {
        final String url = buildUserByUsernameUrl(username);
        final HttpEntity<Void> httpEntity = new HttpEntity<>(headersProvider.get());
        return Optional.ofNullable(restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserSearchRestModel.class)
                        .getBody())
                .flatMap(userSearchRestModel -> userSearchRestModel.getUsers().stream().findFirst())
                .map(UserRestModel::toDomain)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public boolean hasUserPermission(
            final String userId,
            final String permissionName
    ) {
        final String url = buildHasPermissionByUserIdAndPermissionName(userId, permissionName);
        final HttpHeaders headers = headersProvider.get();
        headers.set(AUDIT_USER_HEADER, userId);
        final HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        return Optional.ofNullable(restTemplate.exchange(url, HttpMethod.GET, httpEntity,
                        UserPermissionCheckRestModel.class).getBody())
                .map(UserPermissionCheckRestModel::getResult).orElse(false);
    }

    private String buildUserByUsernameUrl(final String username) {
        return buildUsersResourcePath() + QUERY_STRING_DELIMITER + USERNAME_QUERY_PARAM + username;
    }

    private String buildHasPermissionByUserIdAndPermissionName(final String userId, final String permissionName) {
        return buildUsersResourcePath() + "/" + userId
                + PERMISSIONS_RESOURCE + "/" + permissionName + PERMISSION_NAME_RESOURCE;
    }

    private String buildUsersResourcePath() {
        return config.getUserRepository().getUrl() + USERS_RESOURCE;
    }

}
