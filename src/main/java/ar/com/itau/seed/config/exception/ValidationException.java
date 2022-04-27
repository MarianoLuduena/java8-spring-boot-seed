package ar.com.itau.seed.config.exception;

import ar.com.itau.seed.config.ErrorCode;

public final class ValidationException extends GenericException {

    public ValidationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

}
