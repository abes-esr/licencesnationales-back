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
import fr.abes.licencesnationales.web.dto.ip.PlageIpv6AjouteeDto;
import fr.abes.licencesnationales.web.dto.ip.PlageIpv6AjouteeDto;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
////////////////////////type ip = IPV6 type acces = plage///////////////////////////////
public class PlageIpv6ControllerTest extends LicencesNationalesAPIApplicationTests {


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

    public void plageIpv6Regex(String plageIpv6, boolean validates) throws NoSuchFieldException {
        Field field = PlageIpv6AjouteeDto.class.getDeclaredField("ip");
        javax.validation.constraints.Pattern[] pattern = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        assertEquals(plageIpv6.matches(pattern[0].regexp()),validates);
    }

    @Test
    public void testInvalidPlageIpv6Pattern() throws NoSuchFieldException {
        plageIpv6Regex("192.168.10-2.10", false);
    }

    @Test
    public void testEmptyPlageIpv6FailValidation() throws NoSuchFieldException {
        plageIpv6Regex("", false);
    }

    @Test
    @DisplayName("invalid Pattern plage Ipv6 Fail Validation")
    public void invalidPatternPlageIpv6FailValidation() {

        PlageIpv6AjouteeDto plageIpv6 = new PlageIpv6AjouteeDto();
        plageIpv6.setSiren("123456789");
        plageIpv6.setTypeAcces("ip");
        plageIpv6.setTypeIp("IPV4");
        plageIpv6.setIp("192.68.2.2-1");
        plageIpv6.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv6AjouteeDto>> violations = validator.validate(plageIpv6);
        assertFalse(violations.isEmpty());
    }
    @Test
    @DisplayName("valid Pattern Plage Ipv6 Validation")
    public void validPatternPlageIpv6Validation() {

        PlageIpv6AjouteeDto plageIpv6 = new PlageIpv6AjouteeDto();
        plageIpv6.setSiren("123456789");
        plageIpv6.setTypeAcces("plage");
        plageIpv6.setTypeIp("IPV4");
        plageIpv6.setIp("192.168.10-5.10-4");
        plageIpv6.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv6AjouteeDto>> violations = validator.validate(plageIpv6);
        assertTrue(violations.isEmpty());
    }


    @Test
    @DisplayName("test ajout IPV6")
    @WithMockUser
    public void testAjoutPlageIPV6() throws Exception {
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.doNothing().when(ipService).checkDoublonIpAjouteeDto(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());
        EtablissementEntity etablissementEntity = new EtablissementEntity();
        etablissementEntity.setName("testEtab");
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(etablissementEntity);
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString());

        PlageIpv6AjouteeDto dto = new PlageIpv6AjouteeDto();
        dto.setSiren(siren);
        dto.setIp("192.120.10-5.10-4");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

}
