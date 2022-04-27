package ar.com.itau.seed.adapter.rest.exception;

import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.GenericException;

public final class BadRequestRestClientException extends GenericException {

    public BadRequestRestClientException(ErrorCode errorCode) {
        super(errorCode);
    }

}
