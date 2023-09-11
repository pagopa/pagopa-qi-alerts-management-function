package it.pagopa.qi.alertmanagement.httptrigger;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.pagopa.qi.alertmanagement.models.AppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Azure Functions with Azure Http trigger.
 */
public class Info {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * This function will be invoked when a Http Trigger occurs
     *
     * @return http response containing application information
     */
    @FunctionName("Info")
    public HttpResponseMessage run(
            @HttpTrigger(name = "InfoTrigger",
                    methods = {HttpMethod.GET},
                    route = "info",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(getInfo("/META-INF/maven/it.pagopa/pagopa-qi-alerts-management-function/pom.properties"))
                .build();
    }

    public synchronized AppInfo getInfo(String path) {
        String version = null;
        String name = null;
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass().getResourceAsStream(path);
            if (inputStream != null) {
                properties.load(inputStream);
                version = properties.getProperty("version", null);
                name = properties.getProperty("artifactId", null);
            }
        } catch (Exception e) {
            logger.error("Impossible to retrieve information from pom.properties file.", e);
        }
        return AppInfo.builder().version(version).environment("azure-fn").name(name).build();
    }

}
