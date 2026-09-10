package fr.abes.licencesnationales.core.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EmailService.class})
public class EmailServiceTest {
    @Autowired
    private EmailService service;

    @MockBean
    private RestTemplate restTemplate;

    private StringBuilder jsonResult;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "mailTestRecipient", "ln-admin@abes.fr");
        jsonResult = new StringBuilder("{");
        jsonResult.append("\"app\":\"ln\",");
        jsonResult.append("\"to\":[\"ln-admin@abes.fr\"],");
        jsonResult.append("\"cc\":[\"test2@test2.com\"],");
        jsonResult.append("\"cci\":[],");
        jsonResult.append("\"subject\":\"Test subject\",");
        jsonResult.append("\"text\":\"Test body\"");
        jsonResult.append("}");
    }

    @DisplayName("test création message JSON avec destinataire par défaut")
    @Test
    void testmailToJSON() {
        String to = "test@test.com;test2@test.com";
        String cc = "test2@test2.com";
        String subject = "Test subject";
        String body = "Test body";

        Assertions.assertEquals(jsonResult.toString(), service.mailToJSON(to, cc, subject, body));
    }

    @DisplayName("test création message JSON avec destinataire personnalisé")
    @Test
    void testmailToJSONWithCustomRecipient() {
        ReflectionTestUtils.setField(service, "mailTestRecipient", "dev@abes.fr");

        StringBuilder customJson = new StringBuilder("{");
        customJson.append("\"app\":\"ln\",");
        customJson.append("\"to\":[\"dev@abes.fr\"],");
        customJson.append("\"cc\":[\"test2@test2.com\"],");
        customJson.append("\"cci\":[],");
        customJson.append("\"subject\":\"Test subject\",");
        customJson.append("\"text\":\"Test body\"");
        customJson.append("}");

        String to = "test@test.com;test2@test.com";
        String cc = "test2@test2.com";
        String subject = "Test subject";
        String body = "Test body";

        Assertions.assertEquals(customJson.toString(), service.mailToJSON(to, cc, subject, body));
    }

    @DisplayName("test création message JSON avec conservation du destinataire original")
    @Test
    void testmailToJSONWithOriginalRecipient() {
        ReflectionTestUtils.setField(service, "mailTestRecipient", "original");

        StringBuilder originalJson = new StringBuilder("{");
        originalJson.append("\"app\":\"ln\",");
        originalJson.append("\"to\":[\"test@test.com\",\"test2@test.com\"],");
        originalJson.append("\"cc\":[\"test2@test2.com\"],");
        originalJson.append("\"cci\":[],");
        originalJson.append("\"subject\":\"Test subject\",");
        originalJson.append("\"text\":\"Test body\"");
        originalJson.append("}");

        String to = "test@test.com;test2@test.com";
        String cc = "test2@test2.com";
        String subject = "Test subject";
        String body = "Test body";

        Assertions.assertEquals(originalJson.toString(), service.mailToJSON(to, cc, subject, body));
    }
}
