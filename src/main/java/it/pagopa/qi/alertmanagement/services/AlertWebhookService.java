package it.pagopa.qi.alertmanagement.services;

import it.pagopa.generated.qi.alerts.v1.dto.AlertInfoDto;
import it.pagopa.generated.qi.alerts.v1.dto.QiAlertIngestionRequestDto;
import it.pagopa.generated.qi.events.v1.Alert;
import it.pagopa.generated.qi.events.v1.AlertDetails;
import it.pagopa.qi.alertmanagement.exceptions.AlertParsingException;
import it.pagopa.qi.alertmanagement.utils.AlertParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This service handle received alert request converting it to EventHub events
 */
public class AlertWebhookService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Set<String> ALERT_CODES = Arrays.stream(AlertDetails.Code.values()).map(AlertDetails.Code::toString).collect(Collectors.toSet());

    /**
     * Process input alert request converting to an alert stream
     *
     * @param qiAlertIngestionRequestDto the received alert request
     * @return a stream of converted alerts
     */
    public Stream<Alert> toAlertStream(QiAlertIngestionRequestDto qiAlertIngestionRequestDto) {
        return Optional
                .ofNullable(qiAlertIngestionRequestDto.getAlerts())
                .map(alerts ->
                        alerts
                                .stream()
                                .filter(alert -> {
                                    boolean isResolved = "resolved".equals(alert.getStatus());
                                    boolean isValid = true;
                                    try {
                                        validateAlert(alert);
                                    } catch (AlertParsingException e) {
                                        logger.error("Invalid input alert", e);
                                        isValid = false;
                                    }
                                    boolean toBeFiltered = isResolved || !isValid;
                                    logger.info("Alert with fingerprint: [{}]. Status: [{}], is valid: [{}] to be filtered out -> {}", alert.getFingerprint(), alert.getStatus(), isValid, toBeFiltered);
                                    return !toBeFiltered;
                                })
                                .map(AlertParser::new)
                                .map(this::buildAlertFromWebhook)
                )
                .orElse(Stream.of());

    }

    /**
     * Perform validation against received alert checking for all required fields
     *
     * @param alertInfoDto the received alert to be validated
     */
    private void validateAlert(AlertInfoDto alertInfoDto) {
        AlertParser alertParser = new AlertParser(alertInfoDto);
        String fingerprint = alertParser.getAlertFingerprint().orElse(null);
        //validate alerts.alert.labels
        Arrays.stream(AlertParser.LabelsKey.values()).forEach(
                labelsKey -> alertParser.getLabelValue(labelsKey).orElseThrow(() -> new AlertParsingException(labelsKey.getKey(), null, fingerprint))
        );
        //validate alerts.alert.values
        Arrays.stream(AlertParser.NumericValuesKey.values()).forEach(
                labelsKey -> alertParser.getNumericValue(labelsKey).orElseThrow(() -> new AlertParsingException(labelsKey.getKey(), null, fingerprint))
        );
        //validate start at date
        if (alertParser.getStartAtDate().isEmpty()) {
            throw new AlertParsingException("startAtDate", null, fingerprint);
        }
        //validate code against codes enumeration
        String code = alertParser.getLabelValue(AlertParser.LabelsKey.CODE).orElse(null);
        if (!ALERT_CODES.contains(code)) {
            throw new AlertParsingException(AlertParser.LabelsKey.CODE.getKey(), code, fingerprint);
        }
    }

    /**
     * Build alert taking information from parsed alert
     *
     * @param alertParser parsed alert
     * @return the build event hub alert domain object
     */
    private Alert buildAlertFromWebhook(AlertParser alertParser) {
        return new Alert()
                .withDetails(
                        new AlertDetails()
                                .withCode(AlertDetails.Code.fromValue(alertParser.getLabelValue(AlertParser.LabelsKey.CODE).orElseThrow()))
                                .withOwner(alertParser
                                        .getLabelValue(AlertParser.LabelsKey.OWNER)
                                        .orElseThrow())
                                .withThreshold(alertParser.getNumericValue(AlertParser.NumericValuesKey.THRESHOLD).orElseThrow())
                                .withValue(alertParser.getNumericValue(AlertParser.NumericValuesKey.VALUE).orElseThrow())
                                .withTriggerDate(alertParser.getStartAtDate().orElseThrow())
                );
    }


}
