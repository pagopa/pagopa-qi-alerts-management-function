package it.pagopa.qi.alertmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.generated.qi.alerts.v1.dto.AlertInfoDto;
import it.pagopa.generated.qi.alerts.v1.dto.QiAlertIngestionRequestDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertParsingTests {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Webhook alert example taken from grafana documentation at: <a href="https://grafana.com/docs/grafana/latest/alerting/alerting-rules/manage-contact-points/webhook-notifier/">...</a>
     */
    private final String webhokAlertExample = "{\n" +
            "  \"receiver\": \"My Super Webhook\",\n" +
            "  \"status\": \"firing\",\n" +
            "  \"orgId\": 1,\n" +
            "  \"alerts\": [\n" +
            "    {\n" +
            "      \"status\": \"firing\",\n" +
            "      \"labels\": {\n" +
            "        \"alertname\": \"High memory usage\",\n" +
            "        \"team\": \"blue\",\n" +
            "        \"zone\": \"us-1\"\n" +
            "      },\n" +
            "      \"annotations\": {\n" +
            "        \"description\": \"The system has high memory usage\",\n" +
            "        \"runbook_url\": \"https://myrunbook.com/runbook/1234\",\n" +
            "        \"summary\": \"This alert was triggered for zone us-1\"\n" +
            "      },\n" +
            "      \"startsAt\": \"2021-10-12T09:51:03.157076+02:00\",\n" +
            "      \"endsAt\": \"0001-01-01T00:00:00Z\",\n" +
            "      \"generatorURL\": \"https://play.grafana.org/alerting/1afz29v7z/edit\",\n" +
            "      \"fingerprint\": \"c6eadffa33fcdf37\",\n" +
            "      \"silenceURL\": \"https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT2%2Cteam%3Dblue%2Czone%3Dus-1\",\n" +
            "      \"dashboardURL\": \"\",\n" +
            "      \"panelURL\": \"\",\n" +
            "      \"values\": {\n" +
            "        \"B\": 44.23943737541908,\n" +
            "        \"C\": 1\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"status\": \"firing\",\n" +
            "      \"labels\": {\n" +
            "        \"alertname\": \"High CPU usage\",\n" +
            "        \"team\": \"blue\",\n" +
            "        \"zone\": \"eu-1\"\n" +
            "      },\n" +
            "      \"annotations\": {\n" +
            "        \"description\": \"The system has high CPU usage\",\n" +
            "        \"runbook_url\": \"https://myrunbook.com/runbook/1234\",\n" +
            "        \"summary\": \"This alert was triggered for zone eu-1\"\n" +
            "      },\n" +
            "      \"startsAt\": \"2021-10-12T09:56:03.157076+02:00\",\n" +
            "      \"endsAt\": \"0001-01-01T00:00:00Z\",\n" +
            "      \"generatorURL\": \"https://play.grafana.org/alerting/d1rdpdv7k/edit\",\n" +
            "      \"fingerprint\": \"bc97ff14869b13e3\",\n" +
            "      \"silenceURL\": \"https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT1%2Cteam%3Dblue%2Czone%3Deu-1\",\n" +
            "      \"dashboardURL\": \"\",\n" +
            "      \"panelURL\": \"\",\n" +
            "      \"values\": {\n" +
            "        \"B\": 44.23943737541908,\n" +
            "        \"C\": 1\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"groupLabels\": {},\n" +
            "  \"commonLabels\": {\n" +
            "    \"team\": \"blue\"\n" +
            "  },\n" +
            "  \"commonAnnotations\": {},\n" +
            "  \"externalURL\": \"https://play.grafana.org/\",\n" +
            "  \"version\": \"1\",\n" +
            "  \"groupKey\": \"{}:{}\",\n" +
            "  \"truncatedAlerts\": 0,\n" +
            "  \"title\": \"[FIRING:2]  (blue)\",\n" +
            "  \"state\": \"alerting\",\n" +
            "  \"message\": \"**Firing**\\n\\nLabels:\\n - alertname = T2\\n - team = blue\\n - zone = us-1\\nAnnotations:\\n - description = This is the alert rule checking the second system\\n - runbook_url = https://myrunbook.com\\n - summary = This is my summary\\nSource: https://play.grafana.org/alerting/1afz29v7z/edit\\nSilence: https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT2%2Cteam%3Dblue%2Czone%3Dus-1\\n\\nLabels:\\n - alertname = T1\\n - team = blue\\n - zone = eu-1\\nAnnotations:\\nSource: https://play.grafana.org/alerting/d1rdpdv7k/edit\\nSilence: https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT1%2Cteam%3Dblue%2Czone%3Deu-1\\n\"\n" +
            "}\n";

    @Test
    void shouldParseAlertSuccessfully() throws JsonProcessingException {
        //test
        QiAlertIngestionRequestDto parsedRequest = objectMapper.readValue(webhokAlertExample, QiAlertIngestionRequestDto.class);
        QiAlertIngestionRequestDto expectedRequest = new QiAlertIngestionRequestDto()
                .receiver("My Super Webhook")
                .status("firing")
                .orgId(BigDecimal.ONE)
                .alerts(List.of(
                        new AlertInfoDto()
                                .status("firing")
                                .labels(Map.of("alertname", "High memory usage", "team", "blue", "zone", "us-1"))
                                .annotations(Map.of("description", "The system has high memory usage", "runbook_url", "https://myrunbook.com/runbook/1234", "summary", "This alert was triggered for zone us-1"))
                                .startsAt(OffsetDateTime.parse("2021-10-12T07:51:03.157076Z"))
                                .endsAt(OffsetDateTime.parse("0001-01-01T00:00Z"))
                                .values(Map.of("B", 44.23943737541908, "C", 1.0))
                                .generatorURL(URI.create("https://play.grafana.org/alerting/1afz29v7z/edit"))
                                .fingerprint("c6eadffa33fcdf37")
                                .silenceURL(URI.create("https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT2%2Cteam%3Dblue%2Czone%3Dus-1"))
                                .dashboardURL(URI.create(""))
                                .panelURL(URI.create(""))
                                .imageURL(null),
                        new AlertInfoDto()
                                .status("firing")
                                .labels(Map.of("alertname", "High CPU usage", "team", "blue", "zone", "eu-1"))
                                .annotations(Map.of("description", "The system has high CPU usage", "runbook_url", "https://myrunbook.com/runbook/1234", "summary", "This alert was triggered for zone eu-1"))
                                .startsAt(OffsetDateTime.parse("2021-10-12T07:56:03.157076Z"))
                                .endsAt(OffsetDateTime.parse("0001-01-01T00:00Z"))
                                .values(Map.of("B", 44.23943737541908, "C", 1.0))
                                .generatorURL(URI.create("https://play.grafana.org/alerting/d1rdpdv7k/edit"))
                                .fingerprint("bc97ff14869b13e3")
                                .silenceURL(URI.create("https://play.grafana.org/alerting/silence/new?alertmanager=grafana&matchers=alertname%3DT1%2Cteam%3Dblue%2Czone%3Deu-1"))
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
