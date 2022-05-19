package ar.com.itau.seed.config.exception;

import ar.com.itau.seed.config.ErrorCode;

public final class ForbiddenException extends GenericException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

}
