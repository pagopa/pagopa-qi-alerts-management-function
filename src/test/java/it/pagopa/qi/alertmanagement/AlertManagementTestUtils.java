package it.pagopa.qi.alertmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AlertManagementTestUtils {


    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String getWebhookRequest(String webhookRequestFileName) {
        try {
            return Files.readString(
                    Path.of(
                            new File("./src/test/resources/alertExamples/" + webhookRequestFileName).toURI()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
