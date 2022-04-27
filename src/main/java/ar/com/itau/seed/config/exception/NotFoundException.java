package ar.com.itau.seed.config.exception;

import ar.com.itau.seed.config.ErrorCode;

public final class NotFoundException extends GenericException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
