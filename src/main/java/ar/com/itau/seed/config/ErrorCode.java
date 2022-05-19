package ar.com.itau.seed.config;

public enum ErrorCode {

    INTERNAL_ERROR(100, "Internal server error"),
    BAD_REQUEST(101, "Bad request"),
    RESOURCE_NOT_FOUND(102, "Not found"),
    CHARACTER_BAD_REQUEST(103, "Bad request querying for character"),
    CHARACTER_NOT_FOUND(104, "Star Wars character not found"),
    CHARACTER_TIMEOUT(105, "Timeout when querying character"),
    FORBIDDEN(106, "Not allowed to access the resource");

    private final int value;
    private final String reasonPhrase;

    ErrorCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}
