package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.MockUserDetailsImpl;
import fr.abes.licencesnationales.MockUserUtil;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.core.repository.IpRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.web.dto.ip.Ipv4AjouteeDto;
import fr.abes.licencesnationales.web.dto.password.PasswordEnregistrerWebDto;
import fr.abes.licencesnationales.web.dto.password.PasswordResetWebDto;
import fr.abes.licencesnationales.web.dto.password.PasswordUpdateWebDto;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Slf4j
public class IpControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    private IpController controller;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private IpRepository ipRepository;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private FiltrerAccesServices filtrerAccesServices;

    @MockBean
    private EmailService emailService;

    @MockBean
    private IpService ipService;

    @MockBean
    private SecurityContextHolder securityContextHolder;

    @MockBean
    private MockUserDetailsImpl userDetails;

    /*@MockBean
    private UtilsMapper mapper;*/

    @Autowired
    private ObjectMapper mapper;

    private String siren;


    @Test
    @DisplayName("test ajout IPV4")
    public void testAjoutIPV4() throws Exception {
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn(userDetails.getMockUserDetailsImpl().getSiren());
        //Mockito.when(securityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(userDetails);
        //Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyString());

        Ipv4AjouteeDto dto = new Ipv4AjouteeDto();
        dto.setSiren(siren);
        dto.setIp("192.120.10.10");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutIPV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

}
