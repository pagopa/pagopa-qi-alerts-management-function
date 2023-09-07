package it.pagopa.qi.alertmanagement.exceptions;

/**
 * Exception raised when the parsed {@link it.pagopa.generated.qi.alerts.v1.dto.AlertInfoDto} is invalid
 */
public class AlertParsingException extends RuntimeException {

    /**
     * Constructor
     *
     * @param fieldName   the invalid field name
     * @param fieldValue  the invalid field value
     * @param fingerprint the invalid alert fingerprint
     */
    public AlertParsingException(String fieldName, String fieldValue, String fingerprint) {
        super(String.format("Exception parsing alert with fingerprint: [%s]. Field: [%s] with value: [%s] is invalid", fingerprint, fieldName, fieldValue));


    }
}
