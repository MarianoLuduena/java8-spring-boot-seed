package ar.com.itau.seed.application.port.out;

public interface UserRepository {

    String getUserIdByUsername(String username);

    boolean hasUserPermission(String userId, String permissionName);

}
