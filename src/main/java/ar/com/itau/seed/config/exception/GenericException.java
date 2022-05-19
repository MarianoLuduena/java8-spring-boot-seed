package ar.com.itau.seed.config.exception;

import ar.com.itau.seed.config.ErrorCode;

public abstract class GenericException extends RuntimeException {

    private final ErrorCode errorCode;

    public GenericException(ErrorCode errorCode) {
        super(errorCode.getReasonPhrase());
        this.errorCode = errorCode;
    }

    public ErrorCode getCode() {
        return this.errorCode;
    }

}
