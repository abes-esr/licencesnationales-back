package fr.abes.licencesnationales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = LicencesNationalesAPIApplication.class, properties = {"google.fr.abes.licencesnationales.recaptcha.key.site=XXXX",
        "google.fr.abes.licencesnationales.recaptcha.key.secret=XXXX",
        "google.fr.abes.licencesnationales.recaptcha.key.threshold=3",
        "jwt.token.secret=eyJhbGciOiJIUzI1NiJ9",
        "jwt.token.expirationInMs=86400000"})
public class LicencesNationalesAPIApplicationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    public static ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Route public inexistante")
    public void doWrongPublicRoute() throws Exception {
        mockMvc.perform(get("/v1/authentification/ABCED")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Route privée inexistante")
    public void doWrongPrivatedRoute() throws Exception {
        mockMvc.perform(get("/ABCED")).andExpect(status().isUnauthorized());
    }

}
