package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.core.repository.IpRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.web.dto.ip.Ipv4AjouteeDto;
import fr.abes.licencesnationales.web.dto.ip.PlageIpv4AjouteeDto;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
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


    private Validator validator;

    /*@MockBean
    private UtilsMapper mapper;*/

    @Autowired
    private ObjectMapper mapper;

    private String siren;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    ////////////////////////type ip = IPV4 type acces = ip///////////////////////////////

    public void ipv4Regex(String ipv4, boolean validates) throws NoSuchFieldException {
        Field field = Ipv4AjouteeDto.class.getDeclaredField("ip");
        javax.validation.constraints.Pattern[] annotations = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        assertEquals(ipv4.matches(annotations[0].regexp()),validates);
    }

    @Test
    public void testInvalidIpv4Pattern() throws NoSuchFieldException {
        ipv4Regex("123456", false);
    }
    @Test
    public void testEmptyIpv4FailValidation() throws NoSuchFieldException {
        ipv4Regex("", false);
    }


    @Test
    @DisplayName("invalid Pattern Ipv4 Fail Validation")
    public void invalidPatternIpv4FailValidation() {

        Ipv4AjouteeDto ipv4 = new Ipv4AjouteeDto();
        ipv4.setSiren("123456789");
        ipv4.setTypeAcces("ip");
        ipv4.setTypeIp("IPV4");
        ipv4.setIp("commentaires");
        ipv4.setCommentaires("comm");
        Set<ConstraintViolation<Ipv4AjouteeDto>> violations = validator.validate(ipv4);
        assertFalse(violations.isEmpty());
    }
    @Test
    @DisplayName("validPatternIpv4Validation")
    public void validPatternIpv4Validation() {

        Ipv4AjouteeDto ipv4 = new Ipv4AjouteeDto();
        ipv4.setSiren("123456789");
        ipv4.setTypeAcces("ip");
        ipv4.setTypeIp("IPV4");
        ipv4.setIp("192.168.10.10");
        ipv4.setCommentaires("comm");
        Set<ConstraintViolation<Ipv4AjouteeDto>> violations = validator.validate(ipv4);
        assertTrue(violations.isEmpty());
    }


    @Test
    @DisplayName("test ajout IPV4")
    @WithMockUser
    public void testAjoutIPV4() throws Exception {
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.doNothing().when(ipService).checkDoublonIpAjouteeDto(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());
        EtablissementEntity etablissementEntity = new EtablissementEntity();
        etablissementEntity.setName("testEtab");
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(etablissementEntity);
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString());

        Ipv4AjouteeDto dto = new Ipv4AjouteeDto();
        dto.setSiren(siren);
        dto.setIp("192.120.10.10");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    ////////////////////////type ip = IPV4 type acces = plage///////////////////////////////

    public void plageIpv4Regex(String plageIpv4, boolean validates) throws NoSuchFieldException {
        Field field = PlageIpv4AjouteeDto.class.getDeclaredField("ip");
        javax.validation.constraints.Pattern[] pattern = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        assertEquals(plageIpv4.matches(pattern[0].regexp()),validates);
    }

    @Test
    public void testInvalidPlageIpv4Pattern() throws NoSuchFieldException {
        plageIpv4Regex("192.168.10-2.10", false);
    }

    @Test
    public void testEmptyPlageIpv4FailValidation() throws NoSuchFieldException {
        plageIpv4Regex("", false);
    }

    @Test
    @DisplayName("invalid Pattern plage Ipv4 Fail Validation")
    public void invalidPatternPlageIpv4FailValidation() {

        PlageIpv4AjouteeDto plageIpv4 = new PlageIpv4AjouteeDto();
        plageIpv4.setSiren("123456789");
        plageIpv4.setTypeAcces("ip");
        plageIpv4.setTypeIp("IPV4");
        plageIpv4.setIp("192.68.2.2-1");
        plageIpv4.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv4AjouteeDto>> violations = validator.validate(plageIpv4);
        assertFalse(violations.isEmpty());
    }
    @Test
    @DisplayName("valid Pattern Plage Ipv4 Validation")
    public void validPatternPlageIpv4Validation() {

        PlageIpv4AjouteeDto plageIpv4 = new PlageIpv4AjouteeDto();
        plageIpv4.setSiren("123456789");
        plageIpv4.setTypeAcces("plage");
        plageIpv4.setTypeIp("IPV4");
        plageIpv4.setIp("192.168.10-5.10-4");
        plageIpv4.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv4AjouteeDto>> violations = validator.validate(plageIpv4);
        assertTrue(violations.isEmpty());
    }


    @Test
    @DisplayName("test ajout IPV4")
    @WithMockUser
    public void testAjoutPlageIPV4() throws Exception {
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.doNothing().when(ipService).checkDoublonIpAjouteeDto(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());
        EtablissementEntity etablissementEntity = new EtablissementEntity();
        etablissementEntity.setName("testEtab");
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(etablissementEntity);
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString());

        PlageIpv4AjouteeDto dto = new PlageIpv4AjouteeDto();
        dto.setSiren(siren);
        dto.setIp("192.120.10-5.10-4");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

}
