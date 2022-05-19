package ar.com.itau.seed.mock;

import ar.com.itau.seed.adapter.rest.model.UserPermissionCheckRestModel;
import ar.com.itau.seed.adapter.rest.model.UserRestModel;
import ar.com.itau.seed.adapter.rest.model.UserSearchRestModel;

import java.util.Collections;

public class UserMockFactory {

    private static final String USER_ID = "954";
    private static final String DOC_NUMBER = "14038115";
    private static final String DOC_TYPE = "DNI";
    private static final String USERNAME = "intiman1";

    public static UserSearchRestModel userSearchRestModel() {
        final UserSearchRestModel users = new UserSearchRestModel();
        users.setUsers(Collections.singletonList(userRestModel()));
        return users;
    }

    public static UserSearchRestModel emptyUserSearchRestModel() {
        final UserSearchRestModel users = new UserSearchRestModel();
        users.setUsers(Collections.emptyList());
        return users;
    }

    public static UserPermissionCheckRestModel userPermissionGranted() {
        final UserPermissionCheckRestModel userPermissionCheckRestModel = new UserPermissionCheckRestModel();
        userPermissionCheckRestModel.setResult(true);
        return userPermissionCheckRestModel;
    }

    public static UserPermissionCheckRestModel userPermissionForbidden() {
        final UserPermissionCheckRestModel userPermissionCheckRestModel = new UserPermissionCheckRestModel();
        userPermissionCheckRestModel.setResult(false);
        return userPermissionCheckRestModel;
    }

    private static UserRestModel userRestModel() {
        final UserRestModel user = new UserRestModel();
        user.setId(USER_ID);
        user.setDocumentNumber(DOC_NUMBER);
        user.setDocumentType(DOC_TYPE);
        user.setUsername(USERNAME);
        return user;
    }

}
