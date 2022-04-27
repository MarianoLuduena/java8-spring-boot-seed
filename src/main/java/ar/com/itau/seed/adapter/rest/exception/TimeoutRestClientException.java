package ar.com.itau.seed.adapter.rest.exception;

import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.GenericException;

public final class TimeoutRestClientException extends GenericException {

    public TimeoutRestClientException(ErrorCode errorCode) {
        super(errorCode);
    }

}
