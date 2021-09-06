package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.MockUserUtil;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.dto.password.PasswordEnregistrerWebDto;
import fr.abes.licencesnationales.web.dto.password.PasswordResetWebDto;
import fr.abes.licencesnationales.web.dto.password.PasswordUpdateWebDto;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Slf4j
public class PasswordControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    private PasswordController controller;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private ReCaptchaService reCaptchaService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private EtablissementEntity user;

    @BeforeEach
    public void init() {
        user = new MockUserUtil(passwordEncoder).getMockUser();
    }

    @Test
    @DisplayName("test reset password success")
    public void testResetPasswordSuccess() throws Exception {
        Mockito.when(etablissementService.getUserByMail(Mockito.anyString())).thenReturn(user);
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(user);
        Mockito.doNothing().when(emailService).constructResetTokenEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        PasswordResetWebDto dto = new PasswordResetWebDto();
        dto.setSiren("123456789");
        dto.setEmail("mailTest@test.com");
        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/resetPassword")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test reset password wrong siren")
    public void testResetPasswordWrongSirenEmail() throws Exception {
        Mockito.when(etablissementService.getUserByMail(Mockito.anyString())).thenReturn(null);
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(emailService).constructResetTokenEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        PasswordResetWebDto dto = new PasswordResetWebDto();
        dto.setEmail("mailTest@test.com");
        dto.setSiren("123456789");
        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/resetPassword")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credentials not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Identifiant non connu dans la base ; merci de contacter l’assistance https://stp.abes.fr/node/3?origine=LicencesNationales"));
    }

    @Test
    @DisplayName("test enregistrement password success")
    public void testEnregistrerPasswordSuccess() throws Exception {
        PasswordEnregistrerWebDto dto = new PasswordEnregistrerWebDto();

        ReCaptchaResponse response = new ReCaptchaResponse();
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).then(invocationOnMock -> {
            response.setSuccess(true);
            return response;
        });
        Mockito.when(tokenProvider.validateToken(Mockito.anyString())).thenReturn(true);
        Mockito.doNothing().when(etablissementService).changePasswordFromSiren(Mockito.anyString(), Mockito.anyString());

        //test success
        dto.setPassword("PassWord1!");
        dto.setRecaptcha("testCaptcha");
        dto.setToken("testToken");
        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/enregistrerPassword")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("test update password success")
    public void testUpdatePassword() throws Exception {
        PasswordUpdateWebDto dto = new PasswordUpdateWebDto();

        Mockito.when(tokenProvider.getSirenFromJwtToken(Mockito.any())).thenReturn("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(user);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());

        dto.setOldPassword("OldPass1Test&");
        dto.setNewPassword("NewPass1Test&");
        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/updatePassword")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test update password failed")
    public void testUpdatePasswordFailed() throws Exception {
        PasswordUpdateWebDto dto = new PasswordUpdateWebDto();

        Mockito.when(tokenProvider.getSirenFromJwtToken(Mockito.any())).thenReturn("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(user);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());

        //on teste le cas ou l'ancien mot de passe ne correspond pas à celui en base
        dto.setOldPassword("OldPass1&");
        dto.setNewPassword("NewPass1Test&");
        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/updatePassword")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel."))
                .andExpect(jsonPath("$.debugMessage").value("L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel."));

        //on teste le cas ou le nouveau mot de passe est identique à l'ancien
        dto.setOldPassword("OldPass1Test&");
        dto.setNewPassword("OldPass1Test&");
        this.mockMvc.perform(post("/v1/ln/reinitialisationMotDePasse/updatePassword")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Votre nouveau mot de passe doit être différent de l'ancien"))
                .andExpect(jsonPath("$.debugMessage").value("Votre nouveau mot de passe doit être différent de l'ancien"));
    }

}
