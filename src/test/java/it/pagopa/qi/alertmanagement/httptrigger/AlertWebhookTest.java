package it.pagopa.qi.alertmanagement.httptrigger;

import com.microsoft.azure.functions.*;
import it.pagopa.generated.qi.alerts.v1.dto.ProblemJsonDto;
import it.pagopa.generated.qi.events.v1.Alert;
import it.pagopa.generated.qi.events.v1.AlertDetails;
import it.pagopa.qi.alertmanagement.AlertManagementTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private HttpResponseMessage.Builder httpResponseBuilder;

    @Mock
    private OutputBinding<List<Alert>> outputBinding;

    @Mock
    private ExecutionContext executionContext;

    @Captor
    private ArgumentCaptor<List<Alert>> alertCaptor;

    @Test
    void shouldHandleWebhookRequestSuccessfully() {
        //assertions
        String request = AlertManagementTestUtils.getWebhookRequest("webhook_ok.json");
        given(httpRequestMessage.getBody()).willReturn(Optional.of(request));
        given(executionContext.getInvocationId()).willReturn("invocationId");
        given(httpRequestMessage.createResponseBuilder(HttpStatus.CREATED)).willReturn(httpResponseBuilder);
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
    void shouldReturnHttpResponse400ForInvalidInputRequest() {
        //assertions
        String request = "";
        ProblemJsonDto expectedResponseBody = new ProblemJsonDto()
                .title("Invalid input request")
                .detail("Cannot parse input request")
                .status(400);
        given(httpRequestMessage.getBody()).willReturn(Optional.of(request));
        given(executionContext.getInvocationId()).willReturn("invocationId");
        given(httpRequestMessage.createResponseBuilder(HttpStatus.BAD_REQUEST)).willReturn(httpResponseBuilder);
        given(httpResponseBuilder.body(expectedResponseBody)).willReturn(httpResponseBuilder);
        //test
        alertWebhook.run(httpRequestMessage, outputBinding, executionContext);
        verify(httpRequestMessage, times(1)).getBody();
        verify(executionContext, times(1)).getInvocationId();
        verify(outputBinding, times(0)).setValue(anyList());
    }

    @Test
    void shouldReturnHttpResponse500ForExceptionProcessingTheRequest() {
        //assertions
        ProblemJsonDto expectedResponseBody = new ProblemJsonDto()
                .title("Error processing the request")
                .detail("Unexpected error happened during request processing")
                .status(500);
        given(httpRequestMessage.getBody()).willThrow(new RuntimeException("Error retrieving body"));
        given(executionContext.getInvocationId()).willReturn("invocationId");
        given(httpRequestMessage.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)).willReturn(httpResponseBuilder);
        given(httpResponseBuilder.body(expectedResponseBody)).willReturn(httpResponseBuilder);
        //test
        alertWebhook.run(httpRequestMessage, outputBinding, executionContext);
        verify(httpRequestMessage, times(1)).getBody();
        verify(executionContext, times(1)).getInvocationId();
        verify(outputBinding, times(0)).setValue(anyList());
    }


}