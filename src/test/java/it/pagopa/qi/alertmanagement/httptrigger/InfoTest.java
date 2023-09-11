package it.pagopa.qi.alertmanagement.httptrigger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import it.pagopa.qi.alertmanagement.models.AppInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InfoTest {

    private final Info info = new Info();

    @Mock
    private HttpRequestMessage<Optional<String>> httpRequestMessage;

    @Spy
    private HttpResponseMessage.Builder httpResponseMessageBuilder;

    @Mock
    private HttpResponseMessage httpResponseMessage;

    @Mock
    private ExecutionContext executionContext;

    @Captor
    private ArgumentCaptor<AppInfo> bodyArgumentCaptor;

    @Test
    void shouldReturnAppInfoSuccessfully() {
        //assertions
        given(httpRequestMessage.createResponseBuilder(HttpStatus.OK)).willReturn(httpResponseMessageBuilder);
        given(httpResponseMessageBuilder.header("Content-Type", "application/json")).willReturn(httpResponseMessageBuilder);
        given(httpResponseMessageBuilder.body(bodyArgumentCaptor.capture())).willReturn(httpResponseMessageBuilder);
        given(httpResponseMessageBuilder.build()).willReturn(httpResponseMessage);
        //test
        HttpResponseMessage httpResponseMessage = info.run(httpRequestMessage, executionContext);
        verify(httpRequestMessage, times(1)).createResponseBuilder(HttpStatus.OK);
        verify(httpResponseMessageBuilder, times(1)).header("Content-Type", "application/json");
        verify(httpResponseMessageBuilder, times(1)).body(any());
        verify(httpResponseMessageBuilder, times(1)).build();
        AppInfo response = bodyArgumentCaptor.getValue();
        AppInfo expectedAppInfo = AppInfo.builder().environment("azure-fn").build();
        assertEquals(expectedAppInfo, response);
    }

    @Test
    void shouldParseAppInfoSuccessfullyFromPomProperties() {
        System.out.println(new File(".").getAbsolutePath());
        AppInfo appInfo = info.getInfo("/META-INF/maven/appInfo/pom.properties");
        assertEquals("pagopa-qi-alerts-management-function", appInfo.getName());
        assertEquals("azure-fn", appInfo.getEnvironment());
        assertEquals("0.0.1", appInfo.getVersion());
    }

    @Test
    void shouldReturnInfoForMissingAppInfo() {
        System.out.println(new File(".").getAbsolutePath());
        AppInfo appInfo = info.getInfo("/META-INF/maven/appInfo/missingInfo");
        assertNull(appInfo.getName());
        assertEquals("azure-fn", appInfo.getEnvironment());
        assertNull(appInfo.getVersion());
    }
}
