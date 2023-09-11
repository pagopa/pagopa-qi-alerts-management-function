package it.pagopa.qi.alertmanagement.httptrigger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.pagopa.generated.qi.alerts.v1.dto.QiAlertIngestionRequestDto;
import it.pagopa.generated.qi.events.v1.Alert;
import it.pagopa.qi.alertmanagement.exceptions.InvalidRequestException;
import it.pagopa.qi.alertmanagement.services.AlertWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Alert webhook http trigger function entry-point
 */
public class AlertWebhook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).registerModule(new JavaTimeModule());

    /**
     * This function will be invoked when a Http Trigger occurs
     */
    @FunctionName("AlertWebhook")
    public void run(
            @HttpTrigger(
                    name = "AlertWebhookTrigger",
                    methods = {HttpMethod.POST},
                    route = "qi/alerts",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @EventHubOutput(
                    name = "QiAlerts",
                    eventHubName = "", // blank because the value is included in the connection string
                    connection = "QI_ALERTS_TX_EVENTHUB_CONN_STRING")
            OutputBinding<List<Alert>> queueAlerts,
            final ExecutionContext context) {
        logger.info("Received new alert webhook trigger request with invocation id: [{}]", context.getInvocationId());
        AlertWebhookService alertWebhookService = new AlertWebhookService();
        List<Alert> alerts = request
                .getBody()
                .map(requestBody -> {
                    try {
                        return objectMapper.readValue(requestBody, QiAlertIngestionRequestDto.class);
                    } catch (JsonProcessingException e) {
                        throw new InvalidRequestException("Cannot parse input request", e);
                    }
                })
                .map(alertWebhookService::toAlertList)
                .orElseThrow();
        logger.info("Parsed alerts: {}", alerts.size());
        queueAlerts.setValue(alerts);

    }
}
