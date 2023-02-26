package ar.com.itau.seed.config;

import java.io.Serializable;

public class ErrorCode implements Serializable {

    public static final ErrorCode INTERNAL_ERROR = new ErrorCode("999", "Internal server error");
    public static final ErrorCode BAD_REQUEST = new ErrorCode("101", "Bad request");
    public static final ErrorCode RESOURCE_NOT_FOUND = new ErrorCode("102", "Not found");
    public static final ErrorCode CHARACTER_NOT_FOUND = new ErrorCode("103", "Star Wars character not found");
    public static final ErrorCode FORBIDDEN = new ErrorCode("104", "Not allowed to access the resource");
    public static final ErrorCode TIMEOUT = new ErrorCode("105", "Timeout");

    private final String value;
    private final String reasonPhrase;

    public ErrorCode(final String value, final String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}
