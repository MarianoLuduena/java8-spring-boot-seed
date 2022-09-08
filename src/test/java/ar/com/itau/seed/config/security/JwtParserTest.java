package ar.com.itau.seed.config.security;

import ar.com.itau.seed.mock.AuthenticationMockFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@DisplayName("JwtParser Test")
class JwtParserTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JwtParser parser;

    @BeforeEach
    void setup() {
        parser = new JwtParser(OBJECT_MAPPER);
    }

    @Test
    @DisplayName("should correctly parse an expired JWT")
    void testParseExpiredJwt() throws IOException {
        final JwtParser.Jwt actual = parser.parse(AuthenticationMockFactory.getExpiredJwt());
        final JwtParser.Jwt expected = AuthenticationMockFactory.getParsedExpiredJwt();

        Assertions.assertThat(actual.getPayload().getExpirationDate())
                .isEqualTo(expected.getPayload().getExpirationDate());

        Assertions.assertThat(actual.getPayload().getPreferredUsername())
                .isEqualTo(expected.getPayload().getPreferredUsername());

        Assertions.assertThat(actual.hasExpired()).isTrue();
    }

}
