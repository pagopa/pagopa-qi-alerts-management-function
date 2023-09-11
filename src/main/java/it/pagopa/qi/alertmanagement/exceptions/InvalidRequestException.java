package it.pagopa.qi.alertmanagement.exceptions;

/**
 * Exception thrown in case input request is invalid
 */
public class InvalidRequestException extends RuntimeException {

    /**
     * Constructor
     *
     * @param message the cause of the invalid request exception
     */
    public InvalidRequestException(String message, Throwable e) {
        super(message, e);
    }
}
