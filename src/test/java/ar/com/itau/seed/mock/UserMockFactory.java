package ar.com.itau.seed.mock;

import ar.com.itau.seed.adapter.rest.model.user.UserCompanyRestModel;
import ar.com.itau.seed.adapter.rest.model.user.UserPermissionCheckRestModel;
import ar.com.itau.seed.adapter.rest.model.user.UserRestModel;
import ar.com.itau.seed.adapter.rest.model.user.UserSearchRestModel;
import ar.com.itau.seed.domain.User;

import java.util.Collections;

public class UserMockFactory {

    private static final String USER_ID = "954";
    private static final String DOC_NUMBER = "14038115";
    private static final String DOC_TYPE = "DNI";
    private static final String USERNAME = "intiman1";
    private static final String ENTERPRISE_ID = "540492";
    private static final String ACTIVE_STATE = "1";
    private static final String INACTIVE_STATE = "0";
    private static final String USER_COMPANY_ROLE = "1";

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

    public static User user() {
        return User.builder()
                .id(USER_ID)
                .enterprises(Collections.singletonList(
                        User.Company.builder()
                                .id(ENTERPRISE_ID)
                                .state(ACTIVE_STATE)
                                .role(USER_COMPANY_ROLE)
                                .build()
                ))
                .build();
    }

    public static User inactiveUser() {
        return User.builder()
                .id(USER_ID)
                .enterprises(Collections.singletonList(
                        User.Company.builder()
                                .id(ENTERPRISE_ID)
                                .state(INACTIVE_STATE)
                                .role(USER_COMPANY_ROLE)
                                .build()
                ))
                .build();
    }

    private static UserRestModel userRestModel() {
        final UserRestModel user = new UserRestModel();
        user.setId(USER_ID);
        user.setDocumentNumber(DOC_NUMBER);
        user.setDocumentType(DOC_TYPE);
        user.setUsername(USERNAME);
        user.setEnterprises(Collections.singletonList(userCompanyRestModel()));
        return user;
    }

    private static UserCompanyRestModel userCompanyRestModel() {
        final UserCompanyRestModel model = new UserCompanyRestModel();
        model.setId(ENTERPRISE_ID);
        model.setState(ACTIVE_STATE);
        model.setRole(USER_COMPANY_ROLE);
        return model;
    }

}
