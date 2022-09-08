package ar.com.itau.seed.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;

@Component
public class JwtParser {

    private static final String JWT_SECTION_DELIMITER = "\\.";

    private final ObjectMapper objectMapper;

    public JwtParser(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Jwt parse(final String jwt) throws IOException {
        final String encodedPayload = jwt.split(JWT_SECTION_DELIMITER)[1];
        final JsonNode jsonPayload = objectMapper.readTree(Base64.getUrlDecoder().decode(encodedPayload));
        return new Jwt(new JwtPayload(jsonPayload));
    }

    @AllArgsConstructor
    @Getter
    public static class Jwt {
        private final JwtPayload payload;

        public boolean hasExpired() {
            final LocalDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            return now.isAfter(payload.getExpirationDate());
        }
    }

    @AllArgsConstructor
    public static class JwtPayload {
        private static final String EXPIRATION_TIME = "exp";
        private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";

        private final JsonNode raw;

        public LocalDateTime getExpirationDate() {
            return Instant.ofEpochSecond(raw.get(EXPIRATION_TIME).asLong(0L))
                    .atZone(ZoneOffset.UTC)
                    .toLocalDateTime();
        }

        public String getPreferredUsername() {
            return raw.get(PREFERRED_USERNAME_CLAIM).asText("");
        }
    }

}
