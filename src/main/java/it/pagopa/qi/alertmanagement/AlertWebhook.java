package it.pagopa.qi.alertmanagement;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.pagopa.generated.qi.alerts.v1.dto.QiAlertIngestionRequestDto;
import it.pagopa.generated.qi.events.v1.Alert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertWebhook {

    /**
     * This function will be invoked when a Http Trigger occurs
     */
    @FunctionName("AlertWebhook")
    @EventHubOutput(name = "event", eventHubName = "test", connection = "AzureEventHubConnection")
    public List<Alert> run(
            @HttpTrigger(
                    name = "AlertWebhookTrigger",
                    methods = {HttpMethod.POST},
                    route = "alerts",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<QiAlertIngestionRequestDto>> request,
            final ExecutionContext context) {

        Logger logger = context.getLogger();

        String message = String.format("it.gov.pagopa.project.Example function called at: %s", LocalDateTime.now());
        logger.log(Level.INFO, () -> message);

        return List.of();
    }
}
