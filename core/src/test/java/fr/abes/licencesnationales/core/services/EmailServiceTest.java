package fr.abes.licencesnationales.core.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

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
        jsonResult = new StringBuilder("{");
        jsonResult.append("\"to\":[\"test@test.com\",\"test2@test.com\"],");
        jsonResult.append("\"cc\":[],");
        jsonResult.append("\"cci\":[],");
        jsonResult.append("\"subject\":\"Test subject\",");
        jsonResult.append("\"text\":\"Test body\"");
        jsonResult.append("}");
    }

    @DisplayName("test création message JSON")
    @Test
    void testmailToJSON() {
        String to = "test@test.com;test2@test.com";
        String subject = "Test subject";
        String body = "Test body";

        Assertions.assertEquals(jsonResult.toString(), service.mailToJSON(to, subject, body));
    }
}
