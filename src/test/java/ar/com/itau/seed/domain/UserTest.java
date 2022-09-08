package ar.com.itau.seed.domain;

import ar.com.itau.seed.mock.UserMockFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Test")
class UserTest {

    private static final String ENTERPRISE_ID = "540492";

    @Test
    @DisplayName("when hasCompanyAccess is called it should return true")
    void testUserHasAccessToCompany() {
        final User user = UserMockFactory.user();
        final boolean actual = user.hasCompanyAccess(ENTERPRISE_ID);
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("when hasCompanyAccess is called and the user has nothing to do with the company it should return " +
            "false")
    void testUserHasNoAccessToCompany() {
        final User user = UserMockFactory.user();
        final boolean actual = user.hasCompanyAccess("someId");
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("when hasCompanyAccess is called and the user is inactive for the companty it should return false")
    void testUserInactiveForGivenCompany() {
        final User user = UserMockFactory.inactiveUser();
        final boolean actual = user.hasCompanyAccess(ENTERPRISE_ID);
        Assertions.assertThat(actual).isFalse();
    }

}
