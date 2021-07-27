package fr.abes.licencesnationales.controllers;

import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class EtablissementControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    protected EtablissementController controller;

    public void contextLoads() {
        Assert.assertNotNull(controller);
    }

    @Test
    @WithMockUser
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
                    + "\nrecaptcha\":\"03AGdBq27qViYZpoTfBBy0yu2MOp1eY-yJiI3A86sqQQYo5ItMuW0whvUkR-hXu2OlCRoNFvlOwDUP7Iz_yfB9Y_lBxgEB65VjCHLy3pgBE_QAV2dj1neAMh6ywODWcAgkXmWSL_egKArwUJpTC8bD_SAVfqsh7WgElEjhDCSMCemDq-w6rs9dgnoFHIEivgbowhR7VSrn3-mPr1PIHioTLSB5HFelquwGwEEodjs_ect6ZHQiBgzBVLqnXQmHsqIY54cgznBQAQgFwoZZtdgkmmOSYvYTs6P9MBhZwgsz08XEF8BqQVSqk1tHWahOS6ti0xOputfhG3dGbbdRSgJMveqQlndQXrKzt44FLI-EE6WL9RwgDy72VbJIE-rRd-JT9dfzn8_798XXD3-0CdVWWHkWQxs_EBci_kf8KTUmwroHVANz1MxfwRk9ba3iEXtmCsHWlFk2fkev\n"
                    + "}";
        this.mockMvc.perform(post("/v1/ln/etablissement/creationCompte")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }
}
