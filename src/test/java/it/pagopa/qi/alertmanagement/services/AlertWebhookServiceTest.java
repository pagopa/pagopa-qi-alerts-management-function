package it.pagopa.qi.alertmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.generated.qi.alerts.v1.dto.QiAlertIngestionRequestDto;
import it.pagopa.generated.qi.events.v1.Alert;
import it.pagopa.generated.qi.events.v1.AlertDetails;
import it.pagopa.qi.alertmanagement.AlertManagementTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AlertWebhookServiceTest {

    private final AlertWebhookService alertWebhookService = new AlertWebhookService();

    @Test
    void shouldConvertAlertRequestToAlertEventSuccessfully() throws Exception {
        //pre-condition
        String request = AlertManagementTestUtils.getWebhookRequest("webhook_ok.json");
        QiAlertIngestionRequestDto parsedRequest = AlertManagementTestUtils.OBJECT_MAPPER.readValue(request, QiAlertIngestionRequestDto.class);
        //test
        List<Alert> alerts = alertWebhookService.toAlertList(parsedRequest);
        List<Alert> expectedAlerts = List.of(
                new Alert()
                        .withDetails(
                                new AlertDetails()
                                        .withCode(AlertDetails.Code.TDP)
                                        .withOwner("owner1")
                                        .withTriggerDate("2021-10-12T09:51:03.157076Z")
                                        .withValue(97.999)
                                        .withThreshold(98)
                        ),
                new Alert()
                        .withDetails(
                                new AlertDetails()
                                        .withCode(AlertDetails.Code.TGP)
                                        .withOwner("owner2")
                                        .withTriggerDate("2021-10-12T09:51:03.157076Z")
                                        .withValue(97.997)
                                        .withThreshold(98)
                        )
        );
        //assertions
        assertNotNull(alerts);
        int idx = 0;
        for (Alert alert : alerts) {
            assertEquals(alert, expectedAlerts.get(idx), "Mismatch for alert at idx " + idx);
            idx++;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "webhook_unknown_code.json",
            "webhook_missing_owner_label.json",
            "webhook_missing_threshold_value.json",
            "webhook_missing_startAtDate_value.json",
            "webhook_missing_labels.json",
            "webhook_missing_values.json"
    })
    void shouldFilterOutInvalidEvents(String webhookFileName) throws JsonProcessingException {
        String request = AlertManagementTestUtils.getWebhookRequest(webhookFileName);
        QiAlertIngestionRequestDto parsedRequest = AlertManagementTestUtils.OBJECT_MAPPER.readValue(request, QiAlertIngestionRequestDto.class);
        //test
        List<Alert> alerts = alertWebhookService.toAlertList(parsedRequest);
        assertNotNull(alerts);
        List<Alert> expectedAlerts = List.of(
                new Alert()
                        .withDetails(
                                new AlertDetails()
                                        .withCode(AlertDetails.Code.TGP)
                                        .withOwner("owner2")
                                        .withTriggerDate("2021-10-12T09:51:03.157076Z")
                                        .withValue(97.997)
                                        .withThreshold(98)
                        )
        );
        int idx = 0;
        for (Alert alert : alerts) {
            assertEquals(alert, expectedAlerts.get(idx), "Mismatch for alert at idx " + idx);
            idx++;
        }
    }

    @Test
    void shouldConvertAlertRequestToAlertEventFilteringOutResolvedAlerts() throws Exception {
        //pre-condition
        String request = AlertManagementTestUtils.getWebhookRequest("webhook_ok_with_resolved_alert.json");
        QiAlertIngestionRequestDto parsedRequest = AlertManagementTestUtils.OBJECT_MAPPER.readValue(request, QiAlertIngestionRequestDto.class);
        //test
        List<Alert> alerts = alertWebhookService.toAlertList(parsedRequest);
        List<Alert> expectedAlerts = List.of(
                new Alert()
                        .withDetails(
                                new AlertDetails()
                                        .withCode(AlertDetails.Code.TGP)
                                        .withOwner("owner2")
                                        .withTriggerDate("2021-10-12T09:51:03.157076Z")
                                        .withValue(97.997)
                                        .withThreshold(98)
                        )
        );
        //assertions
        assertNotNull(alerts);
        int idx = 0;
        for (Alert alert : alerts) {
            assertEquals(alert, expectedAlerts.get(idx), "Mismatch for alert at idx " + idx);
            idx++;
        }
    }

    @Test
    void shouldParseWebhookRequestWithNoAlerts() throws Exception {
        //pre-condition
        String request = AlertManagementTestUtils.getWebhookRequest("webhook_ok_with_no_alerts.json");
        QiAlertIngestionRequestDto parsedRequest = AlertManagementTestUtils.OBJECT_MAPPER.readValue(request, QiAlertIngestionRequestDto.class);
        //test
        List<Alert> alerts = alertWebhookService.toAlertList(parsedRequest);
        List<Alert> expectedAlerts = List.of();
        //assertions
        assertNotNull(alerts);
        assertEquals(alerts, expectedAlerts);
    }


}