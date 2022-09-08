package ar.com.itau.seed.adapter.rest;

import ar.com.itau.seed.adapter.rest.model.UserPermissionCheckRestModel;
import ar.com.itau.seed.adapter.rest.model.UserSearchRestModel;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.TestConfig;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.mock.UserMockFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("UserRest Adapter Test")
@Import(TestConfig.class)
@RestClientTest({UserRestAdapter.class})
class UserRestAdapterTest {

    private static final String TRACE_ID_HEADER = "traceId";
    private static final String TRACE_ID = "someTraceId";
    private static final String CHANNEL_ID_HEADER = "channelId";
    private static final String CHANNEL_ID = "I";
    private static final String AUDIT_USER_HEADER = "auditUser";
    private static final String USERS_BASE_URI = "http://localhost:4567/users";
    private static final String USERNAME = "intiman1";
    private static final String USER_ID = "954";
    private static final String PERMISSION_NAME = "someAction";
    private static final String SEARCH_USER_BY_USERNAME_URI = USERS_BASE_URI + "/users?userName=" + USERNAME;
    private static final String USER_PERMISSION_CHECK_URI =
            USERS_BASE_URI + "/users/" + USER_ID + "/permissions/" + PERMISSION_NAME + "/name";

    @Autowired
    private UserRestAdapter client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HeadersProvider headersProvider;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(headersProvider);
        Mockito.when(headersProvider.get()).thenReturn(getMockedHeaders());
    }

    @Test
    @DisplayName("when getUserIdByUsername is called, the adapter should return an id")
    void testGetUserIdByUsernameSuccessfully() throws JsonProcessingException {
        final UserSearchRestModel model = UserMockFactory.userSearchRestModel();
        final String detailString = objectMapper.writeValueAsString(model);
        server.expect(requestTo(SEARCH_USER_BY_USERNAME_URI))
                .andExpect(header(CHANNEL_ID_HEADER, CHANNEL_ID))
                .andExpect(header(TRACE_ID_HEADER, TRACE_ID))
                .andRespond(withSuccess(detailString, MediaType.APPLICATION_JSON));
        final String actual = client.getUserIdByUsername(USERNAME);
        Assertions.assertThat(actual).isEqualTo(model.getUsers().get(0).getId());
    }

    @Test
    @DisplayName("when getUserIdByUsername is called and no user is found, the adapter should throw a NotFoundException")
    void testGetUserIdByUsernameNotFound() throws JsonProcessingException {
        final UserSearchRestModel model = UserMockFactory.emptyUserSearchRestModel();
        final String detailString = objectMapper.writeValueAsString(model);
        server.expect(requestTo(SEARCH_USER_BY_USERNAME_URI))
                .andExpect(header(CHANNEL_ID_HEADER, CHANNEL_ID))
                .andExpect(header(TRACE_ID_HEADER, TRACE_ID))
                .andRespond(withSuccess(detailString, MediaType.APPLICATION_JSON));

        final Throwable thrown = Assertions.catchThrowable(() -> client.getUserIdByUsername(USERNAME));

        Assertions.assertThat(thrown)
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.RESOURCE_NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("when hasUserPermission is called, the adapter should return the server's response")
    void testHasUserPermissionSuccessfully() throws JsonProcessingException {
        final UserPermissionCheckRestModel model = UserMockFactory.userPermissionGranted();
        final String detailString = objectMapper.writeValueAsString(model);
        server.expect(requestTo(USER_PERMISSION_CHECK_URI))
                .andExpect(header(CHANNEL_ID_HEADER, CHANNEL_ID))
                .andExpect(header(TRACE_ID_HEADER, TRACE_ID))
                .andExpect(header(AUDIT_USER_HEADER, USER_ID))
                .andRespond(withSuccess(detailString, MediaType.APPLICATION_JSON));

        final boolean actual = client.hasUserPermission(USER_ID, PERMISSION_NAME);
        Assertions.assertThat(actual).isEqualTo(model.getResult());
    }

    private HttpHeaders getMockedHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CHANNEL_ID_HEADER, CHANNEL_ID);
        headers.set(TRACE_ID_HEADER, TRACE_ID);
        return headers;
    }

}
