package it.pagopa.qi.alertmanagement.httptrigger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.OutputBinding;
import it.pagopa.generated.qi.events.v1.Alert;
import it.pagopa.generated.qi.events.v1.AlertDetails;
import it.pagopa.qi.alertmanagement.AlertManagementTestUtils;
import it.pagopa.qi.alertmanagement.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertWebhookTest {

    private final AlertWebhook alertWebhook = new AlertWebhook();

    @Mock
    private HttpRequestMessage<Optional<String>> httpRequestMessage;

    @Mock
    private OutputBinding<List<Alert>> outputBinding;

    @Mock
    private ExecutionContext executionContext;

    @Captor
    private ArgumentCaptor<List<Alert>> alertCaptor;

    @Test
    void shouldHandleWebhookRequestSuccessfully() throws Exception {
        //assertions
        String request = AlertManagementTestUtils.getWebhookRequest("webhook_ok.json");
        given(httpRequestMessage.getBody()).willReturn(Optional.of(request));
        given(executionContext.getInvocationId()).willReturn("invocationId");
        willDoNothing().given(outputBinding).setValue(alertCaptor.capture());
        //test
        alertWebhook.run(httpRequestMessage, outputBinding, executionContext);

        verify(httpRequestMessage, times(1)).getBody();
        verify(executionContext, times(1)).getInvocationId();
        verify(outputBinding, times(1)).setValue(anyList());
        List<Alert> alerts = alertCaptor.getValue();
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

        assertNotNull(alerts);
        int idx = 0;
        for (Alert alert : alerts) {
            assertEquals(alert, expectedAlerts.get(idx), "Mismatch for alert at idx " + idx);
            idx++;
        }

    }

    @Test
    void shouldThrowInvalidRequestExceptionForInvalidRequestReceived() throws Exception {
        //assertions
        String request = "";
        given(httpRequestMessage.getBody()).willReturn(Optional.of(request));
        given(executionContext.getInvocationId()).willReturn("invocationId");
        //test
        assertThrows(InvalidRequestException.class, () -> alertWebhook.run(httpRequestMessage, outputBinding, executionContext));
        verify(httpRequestMessage, times(1)).getBody();
        verify(executionContext, times(1)).getInvocationId();
        verify(outputBinding, times(0)).setValue(anyList());


    }

}