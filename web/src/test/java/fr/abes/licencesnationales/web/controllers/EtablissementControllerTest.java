package fr.abes.licencesnationales.web.controllers;

import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import fr.abes.licencesnationales.core.services.ContactService;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class EtablissementControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    protected EtablissementController controller;

    @MockBean
    private EtablissementService service;

    @MockBean
    private ReCaptchaService reCaptchaService;

    @MockBean
    private ContactService contactService;

    @MockBean
    private EmailService emailService;

    public void contextLoads() {
        Assert.assertNotNull(controller);
    }

    @Test
    @DisplayName("test création de compte")
    public void testCreationCompte() throws Exception {
        String json = "{\n"
                    + "\"nom\":\"Etab de test 32\",\n"
                    + "\"siren\":\"123456789\",\n"
                    + "\"typeEtablissement\":\"EPIC/EPST\",\n"
                    + "\"idAbes\":\"\",\n"
                    + "\"nomContact\":\"teest\",\n"
                    + "\"prenomContact\":\"tteeest\",\n"
                    + "\"adresseContact\":\"62 rue du test\",\n"
                    + "\"boitePostaleContact\":\"\",\n"
                    + "\"codePostalContact\":\"34000\",\n"
                    + "\"villeContact\":\"Montpellier\",\n"
                    + "\"cedexContact\":\"\",\n"
                    + "\"telephoneContact\":\"0606060606\",\n"
                    + "\"mailContact\":\"chambon@abes.fr\",\n"
                    + "\"motDePasse\":\"@Password33\",\n"
                    + "\"roleContact\":\"\",\n"
                    + "\"recaptcha\":\"ksdjfklsklfjhskjdfhklf\"\n"
                    + "}";

        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction("creationCompte");
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

        Mockito.when(contactService.existeMail(Mockito.anyString())).thenReturn(false);

        Mockito.doNothing().when(emailService).constructCreationCompteEmailAdmin(Mockito.any(Locale.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailUser(Mockito.any(Locale.class), Mockito.anyString());

        this.mockMvc.perform(post("/v1/ln/etablissement/creationCompte")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test création de compte avec Exception")
    public void testCreationCompteThrow() throws Exception {
        String json = "{\n"
                + "\"nom\":\"Etab de test 32\",\n"
                + "\"siren\":\"123456789\",\n"
                + "\"typeEtablissement\":\"EPIC/EPST\",\n"
                + "\"idAbes\":\"\",\n"
                + "\"nomContact\":\"teest\",\n"
                + "\"prenomContact\":\"tteeest\",\n"
                + "\"adresseContact\":\"62 rue du test\",\n"
                + "\"boitePostaleContact\":\"\",\n"
                + "\"codePostalContact\":\"34000\",\n"
                + "\"villeContact\":\"Montpellier\",\n"
                + "\"cedexContact\":\"\",\n"
                + "\"telephoneContact\":\"0606060606\",\n"
                + "\"mailContact\":\"chambon@abes.fr\",\n"
                + "\"motDePasse\":\"@Password33\",\n"
                + "\"roleContact\":\"\",\n"
                + "\"recaptcha\":\"ksdjfklsklfjhskjdfhklf\"\n"
                + "}";

        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction("creationCompte");
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

        Mockito.when(contactService.existeMail(Mockito.anyString())).thenReturn(true);

        this.mockMvc.perform(post("/v1/ln/etablissement/creationCompte")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").isString());
    }

    @Test
    @DisplayName("test liste établissements")
    @WithMockUser(authorities = {"admin"})
    public void testListEtab() throws Exception {
        List<EtablissementEntity> etabList = new ArrayList<>();
        etabList.add(new EtablissementEntity(1L, "testNom", "123456789", "testType", "1", null, null));
        etabList.add(new EtablissementEntity(2L, "testNom", "123456789", "testType", "1", null, null));
        etabList.add(new EtablissementEntity(3L, "testNom", "123456789", "testType", "1", null, null));
        Mockito.when(service.findAll()).thenReturn(etabList);

        this.mockMvc.perform(get("/v1/ln/etablissement/getListEtab")).andExpect(status().isOk());
        this.mockMvc.perform(post("/v1/ln/etablissement/getListEtab")).andExpect(status().isMethodNotAllowed());
    }

}
