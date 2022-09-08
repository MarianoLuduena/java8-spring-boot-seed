package ar.com.itau.seed.mock;

import ar.com.itau.seed.config.security.JwtParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class AuthenticationMockFactory {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String USERNAME = "intiman1";

    private static final String PAYLOAD =
            "eyJqdGkiOiI5MzZmYzFhMi1iMWJhLTQ0NDMtOWU1ZC0wYjE2NjkyODAyZTYiLCJleHAiOjE2NTI5NzM3OTAsIm5iZiI6MCwiaWF0" +
                    "IjoxNjUyOTcxOTkwLCJpc3MiOiJodHRwczovL3Nzby1yaHNzby1pbnRlLmFwcHMub2NwLW5wLnNpcy5hZC5iaWEuaXRh" +
                    "dS9hdXRoL3JlYWxtcy9Ib21lQmFua2luZ0VtcHJlc2FzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjYzOGY3ZTIzLWQx" +
                    "NjYtNDdiYi05Njk2LTc3ZTA5NzBmNGJjMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1lcmN1cnkiLCJhdXRoX3RpbWUi" +
                    "OjAsInNlc3Npb25fc3RhdGUiOiJiYzc0NDRkNS00NjE2LTQyYjktYjFkMy1iMmE2NzFiODFiNTAiLCJhY3IiOiIxIiwi" +
                    "YWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1h" +
                    "X2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5h" +
                    "Z2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxl" +
                    "IGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJpbnRpbWFuMSJ9";

    private static final String JWT =
            "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3RmxTbnJScVZMUV9ObWo5MTFhZF8taE1pOE1TQ0xrXzVHQXdJ" +
                    "dC05eUNNIn0." + PAYLOAD +
                    ".gq8-YCLa3gDAEKYG93gUBvAle6g58jCB2kXIJeLWPXI6iUpANzUcuPv8qjbOEYh04PpyQwxe696Lnuhspxw9iaDhLA9" +
                    "BeyHhP8osTlyMHBgN5uLwdHia9zOjcFwjx_blPU5jPVKxEVp-YOLXnOEKHKiIRa26UtHDZ-WGagfcZuHEyWSZ8jRzOAY" +
                    "ef6LLAylelNeXGta4AcfpazLT8abJtsVu4ylRjdSIkzPj2vSrKBNZvrj0N2kygiFOu2Wju_cfv2vIhRjRgncyv72xTck" +
                    "Tnzp3JLBIlP7X4xj1_1KzlJ7sKbhrFh0SAFLOv6b8kJoI2aCxBLYca40rG2g-exL91w";

    public static String getPreferredUsername() {
        return USERNAME;
    }

    public static String getExpiredJwt() {
        return JWT;
    }

    @SneakyThrows
    public static JwtParser.Jwt getParsedExpiredJwt() {
        final JwtParser.JwtPayload payload =
                new JwtParser.JwtPayload(OBJECT_MAPPER.readTree(Base64.getUrlDecoder().decode(PAYLOAD)));
        return new JwtParser.Jwt(payload);
    }

    @SneakyThrows
    public static JwtParser.Jwt getValidJwt() {
        final long expiration = Instant.now().plus(1L, ChronoUnit.DAYS).getEpochSecond();
        final String validPayload = "{ \"exp\": " + expiration + ", \"preferred_username\": \"" + USERNAME + "\" }";
        final JwtParser.JwtPayload payload = new JwtParser.JwtPayload(OBJECT_MAPPER.readTree(validPayload));
        return new JwtParser.Jwt(payload);
    }

}
