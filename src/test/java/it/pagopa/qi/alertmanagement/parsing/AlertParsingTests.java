package it.pagopa.qi.alertmanagement.parsing;

import it.pagopa.generated.qi.alerts.v1.dto.AlertInfoDto;
import it.pagopa.generated.qi.alerts.v1.dto.QiAlertIngestionRequestDto;
import it.pagopa.generated.qi.events.v1.AlertDetails;
import it.pagopa.qi.alertmanagement.AlertManagementTestUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertParsingTests {

    /**
     * Webhook alert example taken from grafana documentation at: <a href="https://grafana.com/docs/grafana/latest/alerting/alerting-rules/manage-contact-points/webhook-notifier/">...</a>
     */


    @Test
    void shouldParseAlertSuccessfully() throws Exception {
        //test
        String request = AlertManagementTestUtils.getWebhookRequest("webhook_ok.json");
        QiAlertIngestionRequestDto parsedRequest = AlertManagementTestUtils.OBJECT_MAPPER.readValue(request, QiAlertIngestionRequestDto.class);
        QiAlertIngestionRequestDto expectedRequest = new QiAlertIngestionRequestDto()
                .receiver("My Super Webhook")
                .status("firing")
                .orgId(BigDecimal.ONE)
                .alerts(List.of(
                        new AlertInfoDto()
                                .status("firing")
                                .labels(Map.of("owner", "owner1", "code", AlertDetails.Code.TDP.toString()))
                                .annotations(Map.of("description", "annotation", "runbook_url", "https://url", "summary", "summary"))
                                .startsAt(OffsetDateTime.parse("2021-10-12T09:51:03.157076Z"))
                                .endsAt(OffsetDateTime.parse("0001-01-01T00:00Z"))
                                .values(Map.of("threshold", 98.0, "value", 97.999))
                                .generatorURL(URI.create("https://url"))
                                .fingerprint("c6eadffa33fcdf37")
                                .silenceURL(URI.create("https://url"))
                                .dashboardURL(URI.create(""))
                                .panelURL(URI.create(""))
                                .imageURL(null),
                        new AlertInfoDto()
                                .status("firing")
                                .labels(Map.of("owner", "owner2", "code", AlertDetails.Code.TGP.toString()))
                                .annotations(Map.of("description", "annotation", "runbook_url", "https://url", "summary", "summary"))
                                .startsAt(OffsetDateTime.parse("2021-10-12T09:51:03.157076Z"))
                                .endsAt(OffsetDateTime.parse("0001-01-01T00:00Z"))
                                .values(Map.of("threshold", 98.0, "value", 97.997))
                                .generatorURL(URI.create("https://url"))
                                .fingerprint("c6eadffa33fcdf02")
                                .silenceURL(URI.create("https://url"))
                                .dashboardURL(URI.create(""))
                                .panelURL(URI.create(""))
                                .imageURL(null)
                ))
                .groupLabels(Map.of())
                .commonLabels(Map.of("team", "blue"))
                .commonAnnotations(Map.of())
                .externalURL(URI.create("https://play.grafana.org/"))
                .version("1")
                .groupKey("{}:{}")
                .truncatedAlerts(BigDecimal.ZERO)
                .title("[FIRING:2]  (blue)")
                .state("alerting")
                .message("**Firing**\n\nLabels:\n - alertname = T2\n - team = blue\n - zone = us-1\nAnnotations:\n - description = This is the alert rule checking the second system\n - runbook_url = https://myrunbook.com\n - summary = This is my summary\nSource: https://play.grafana.org/alerting/1afz29v7z/edit\nSilence: https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT2%2Cteam%3Dblue%2Czone%3Dus-1\n\nLabels:\n - alertname = T1\n - team = blue\n - zone = eu-1\nAnnotations:\nSource: https://play.grafana.org/alerting/d1rdpdv7k/edit\nSilence: https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT1%2Cteam%3Dblue%2Czone%3Deu-1\n");
        assertEquals(expectedRequest, parsedRequest);

    }
}
