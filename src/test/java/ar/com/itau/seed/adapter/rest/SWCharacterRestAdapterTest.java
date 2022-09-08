package ar.com.itau.seed.adapter.rest;

import ar.com.itau.seed.adapter.rest.exception.BadRequestRestClientException;
import ar.com.itau.seed.adapter.rest.exception.RestClientGenericException;
import ar.com.itau.seed.adapter.rest.exception.TimeoutRestClientException;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.TestConfig;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.domain.SWCharacter;
import ar.com.itau.seed.mock.SWCharacterMockFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

@DisplayName("SWCharacterRest Adapter Test")
@Import(TestConfig.class)
@RestClientTest({SWCharacterRestAdapter.class})
class SWCharacterRestAdapterTest {

    private static final String BASE_URI = "http://localhost:12345/people/{id}";
    private static final int SW_CHARACTER_ID = 4;
    private static final String EXPECTED_URI = BASE_URI.replace("{id}", String.valueOf(SW_CHARACTER_ID));

    @Autowired
    private SWCharacterRestAdapter client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("when getById is called, the adapter should return a Star Wars character")
    void testGetByIdSuccessfully() throws JsonProcessingException {
        final String detailString = objectMapper.writeValueAsString(SWCharacterMockFactory.getSWCharacterRestModel());
        server.expect(requestTo(EXPECTED_URI)).andRespond(withSuccess(detailString, MediaType.APPLICATION_JSON));
        final SWCharacter expected = SWCharacterMockFactory.getSWCharacter();
        final SWCharacter actual = client.getById(SW_CHARACTER_ID);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("when getById is called and the server replies with No Content then the adapter should throw a " +
            "NotFoundException")
    void testGetByIdNoContent() {
        server.expect(requestTo(EXPECTED_URI)).andRespond(withNoContent());
        final Throwable thrown = Assertions.catchThrowable(() -> client.getById(SW_CHARACTER_ID));
        Assertions.assertThat(thrown).isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.RESOURCE_NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("when getById is called and the server replies with Bad Request then the adapter should throw a " +
            "BadRequestRestClientException")
    void testGetByIdBadRequest() {
        server.expect(requestTo(EXPECTED_URI)).andRespond(withBadRequest());
        final Throwable thrown = Assertions.catchThrowable(() -> client.getById(SW_CHARACTER_ID));
        Assertions.assertThat(thrown).isExactlyInstanceOf(BadRequestRestClientException.class)
                .hasMessage(ErrorCode.CHARACTER_BAD_REQUEST.getReasonPhrase());
    }

    @Test
    @DisplayName("when getById is called and the server replies with Not Found then the adapter should throw a " +
            "NotFoundException")
    void testGetByIdNotFound() {
        server.expect(requestTo(EXPECTED_URI)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        final Throwable thrown = Assertions.catchThrowable(() -> client.getById(SW_CHARACTER_ID));
        Assertions.assertThat(thrown).isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.CHARACTER_NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("when getById is called and the server replies with Gateway Timeout then the adapter should throw " +
            "a TimeoutRestClientException")
    void testGetByIdGatewayTimeout() {
        server.expect(requestTo(EXPECTED_URI)).andRespond(withStatus(HttpStatus.GATEWAY_TIMEOUT));
        final Throwable thrown = Assertions.catchThrowable(() -> client.getById(SW_CHARACTER_ID));
        Assertions.assertThat(thrown).isExactlyInstanceOf(TimeoutRestClientException.class)
                .hasMessage(ErrorCode.CHARACTER_TIMEOUT.getReasonPhrase());
    }

    @Test
    @DisplayName("when getById is called and the server replies with Internal Server Error then the adapter " +
            "should throw a RestClientGenericException")
    void testGetByIdInternalServerError() {
        server.expect(requestTo(EXPECTED_URI)).andRespond(withServerError());
        final Throwable thrown = Assertions.catchThrowable(() -> client.getById(SW_CHARACTER_ID));
        Assertions.assertThat(thrown).isExactlyInstanceOf(RestClientGenericException.class)
                .hasMessage(ErrorCode.INTERNAL_ERROR.getReasonPhrase());
    }

}
