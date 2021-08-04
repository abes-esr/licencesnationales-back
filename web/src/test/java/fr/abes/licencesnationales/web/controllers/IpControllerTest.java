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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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



}
