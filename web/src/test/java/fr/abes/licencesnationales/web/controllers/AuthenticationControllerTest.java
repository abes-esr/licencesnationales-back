package fr.abes.licencesnationales.web.controllers;

import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.dto.authentification.ConnexionRequestDto;
import fr.abes.licencesnationales.web.dto.authentification.MotDePasseOublieRequestDto;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.service.ReCaptchaAction;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AuthenticationControllerTest extends LicencesNationalesAPIApplicationTests {

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private ReCaptchaService reCaptchaService;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Authentification - route inexistante")
    public void doWrongRoute() throws Exception {
        mockMvc.perform(get("/v1/authentification/test")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Connexion - mauvaise méthode HTTP")
    public void doLoginGet() throws Exception {
        mockMvc.perform(get("/v1/authentification/connexion")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Connexion - json vide")
    public void doLoginPost() throws Exception {
        mockMvc.perform(post("/v1/authentification/connexion")).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Required request body is missing"))
                );
    }

    @Test
    @DisplayName("Connexion - identifiant valide")
    public void testLoginSuccess() throws Exception {

        String motDePasse = "password";
        String motDePasseCrypte = passwordEncoder.encode(motDePasse);

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", motDePasseCrypte);
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);

        Mockito.when(etablissementService.getFirstBySiren(etabIn.getSiren())).thenReturn(etabIn);

        ConnexionRequestDto request = new ConnexionRequestDto();
        request.setLogin(etabIn.getSiren());
        request.setPassword(motDePasse);

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/authentification/connexion")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Connexion - mauvais login / mdp")
    public void testLoginFailed() throws Exception {
        String motDePasse = "password";
        String motDePasseCrypte = passwordEncoder.encode(motDePasse);

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", motDePasseCrypte);
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);

        Mockito.when(etablissementService.getFirstBySiren(etabIn.getSiren())).thenReturn(etabIn);

        ConnexionRequestDto request = new ConnexionRequestDto();
        request.setLogin("123456788");
        request.setPassword(motDePasse);

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/authentification/connexion")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credentials not valid"))
                .andExpect(content().string(containsString("Bad credentials")));
    }

    @Test
    @DisplayName("Connexion - mauvais pattern 1 ")
    public void testValidatorMarche() throws Exception {

        ConnexionRequestDto request = new ConnexionRequestDto();
        request.setLogin("");
        request.setPassword("");

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/authentification/connexion")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(content().string(containsString("Le SIREN doit contenir 9 chiffres")))
                .andExpect(content().string(containsString("SIREN obligatoire (login)")))
                .andExpect(content().string(containsString("Mot de passe obligatoire (password)")));
    }

    @Test
    @DisplayName("Connexion - mauvais pattern 2")
    public void testValidatorTheCredentialsAreNotValid() throws Exception {
        ConnexionRequestDto request = new ConnexionRequestDto();
        request.setLogin("123");
        request.setPassword("password");

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/authentification/connexion")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(content().string(containsString("Le SIREN doit contenir 9 chiffres")));
    }

    @Test
    @DisplayName("Mot de passe oublié - SIREN valide")
    public void testMotDePasseOublieSirenValide() throws Exception {

        // Mock user
        String motDePasse = "password";
        String motDePasseCrypte = passwordEncoder.encode(motDePasse);

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", motDePasseCrypte);
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);

        Mockito.when(etablissementService.getFirstBySiren(etabIn.getSiren())).thenReturn(etabIn);

        // Mock ReCaptcha
        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction(ReCaptchaAction.MOT_DE_PASSE_OUBLIE);

        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.doNothing().when(emailService).constructResetTokenEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        // Début
        MotDePasseOublieRequestDto request = new MotDePasseOublieRequestDto();
        request.setSiren(etabIn.getSiren());
        request.setRecaptcha("4566");

        this.mockMvc.perform(post("/v1/authentification/motDePasseOublie")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Un mail avec un lien de réinitialisation vous a été envoyé"));
    }

    @Test
    @DisplayName("Mot de passe oublié - Email valide")
    public void testMotDePasseOublieEmailValide() throws Exception {

        // Mock user
        String motDePasse = "password";
        String motDePasseCrypte = passwordEncoder.encode(motDePasse);

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", motDePasseCrypte);
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);

        Mockito.when(etablissementService.getUserByMail(etabIn.getContact().getMail())).thenReturn(etabIn);

        // Mock ReCaptcha
        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction(ReCaptchaAction.MOT_DE_PASSE_OUBLIE);

        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.doNothing().when(emailService).constructResetTokenEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        // Début
        MotDePasseOublieRequestDto request = new MotDePasseOublieRequestDto();
        request.setEmail(etabIn.getContact().getMail());
        request.setRecaptcha("4566");

        this.mockMvc.perform(post("/v1/authentification/motDePasseOublie")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Un mail avec un lien de réinitialisation vous a été envoyé"));
    }

    @Test
    @DisplayName("Mot de passe oublié - Json vide")
    public void testMotDePasseOublieJsonVide() throws Exception {

        // Mock ReCaptcha
        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction(ReCaptchaAction.MOT_DE_PASSE_OUBLIE);

        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

        // Début
        MotDePasseOublieRequestDto request = new MotDePasseOublieRequestDto();


        this.mockMvc.perform(post("/v1/authentification/motDePasseOublie")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le champs 'recaptcha' est obligatoire"));
    }

    @Test
    @DisplayName("Mot de passe oublié - Sans Email ou SIREN")
    public void testMotDePasseOublieSansSirenEmail() throws Exception {

        // Mock ReCaptcha
        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction(ReCaptchaAction.MOT_DE_PASSE_OUBLIE);

        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

        // Début
        MotDePasseOublieRequestDto request = new MotDePasseOublieRequestDto();
        request.setRecaptcha("1234");

        this.mockMvc.perform(post("/v1/authentification/motDePasseOublie")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Au moins un des champs 'siren' ou 'email' est obligatoire"));
    }
//
//    @Test
//    @DisplayName("test reset password success")
//    public void testResetPasswordSuccess() throws Exception {
//        Mockito.when(etablissementService.getUserByMail(Mockito.anyString())).thenReturn(user);
//        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(user);
//        Mockito.doNothing().when(emailService).constructResetTokenEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
//
//        PasswordResetWebDto dto = new PasswordResetWebDto();
//        dto.setSiren("123456789");
//        dto.setEmail("mailTest@test.com");
//        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/resetPassword")
//                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("test reset password wrong siren")
//    public void testResetPasswordWrongSirenEmail() throws Exception {
//        Mockito.when(etablissementService.getUserByMail(Mockito.anyString())).thenReturn(null);
//        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(null);
//        Mockito.doNothing().when(emailService).constructResetTokenEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
//        PasswordResetWebDto dto = new PasswordResetWebDto();
//        dto.setEmail("mailTest@test.com");
//        dto.setSiren("123456789");
//        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/resetPassword")
//                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Credentials not valid"))
//                .andExpect(jsonPath("$.debugMessage").value("Identifiant non connu dans la base ; merci de contacter l’assistance https://stp.abes.fr/node/3?origine=LicencesNationales"));
//    }
//
//    @Test
//    @DisplayName("test enregistrement password success")
//    public void testEnregistrerPasswordSuccess() throws Exception {
//        PasswordEnregistrerWebDto dto = new PasswordEnregistrerWebDto();
//
//        ReCaptchaResponse response = new ReCaptchaResponse();
//        /*Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).then(invocationOnMock -> {
//            response.setSuccess(true);
//            return response;
//        });*/
//        Mockito.when(tokenProvider.validateToken(Mockito.anyString())).thenReturn(true);
//        Mockito.doNothing().when(etablissementService).changePasswordFromSiren(Mockito.anyString(), Mockito.anyString());
//
//        //test success
//        dto.setPassword("PassWord1!");
//        dto.setRecaptcha("testCaptcha");
//        dto.setToken("testToken");
//        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/enregistrerPassword")
//                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
//
//    }
//
//    @Test
//    @DisplayName("test update password success")
//    public void testUpdatePassword() throws Exception {
//        PasswordUpdateWebDto dto = new PasswordUpdateWebDto();
//
//        Mockito.when(tokenProvider.getSirenFromJwtToken(Mockito.any())).thenReturn("123456789");
//        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(user);
//        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
//
//        dto.setOldPassword("OldPass1Test&");
//        dto.setNewPassword("NewPass1Test&");
//        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/updatePassword")
//                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("test update password failed")
//    public void testUpdatePasswordFailed() throws Exception {
//        PasswordUpdateWebDto dto = new PasswordUpdateWebDto();
//
//        Mockito.when(tokenProvider.getSirenFromJwtToken(Mockito.any())).thenReturn("123456789");
//        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(user);
//        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
//
//        //on teste le cas ou l'ancien mot de passe ne correspond pas à celui en base
//        dto.setOldPassword("OldPass1&");
//        dto.setNewPassword("NewPass1Test&");
//        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/updatePassword")
//                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel."))
//                .andExpect(jsonPath("$.debugMessage").value("L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel."));
//
//        //on teste le cas ou le nouveau mot de passe est identique à l'ancien
//        dto.setOldPassword("OldPass1Test&");
//        dto.setNewPassword("OldPass1Test&");
//        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/updatePassword")
//                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Votre nouveau mot de passe doit être différent de l'ancien"))
//                .andExpect(jsonPath("$.debugMessage").value("Votre nouveau mot de passe doit être différent de l'ancien"));
//    }
}
