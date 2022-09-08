package ar.com.itau.seed.application.port.out;

import ar.com.itau.seed.domain.User;

public interface UserRepository {

    User getUserByUsername(String username);

    boolean hasUserPermission(String userId, String permissionName);

}
