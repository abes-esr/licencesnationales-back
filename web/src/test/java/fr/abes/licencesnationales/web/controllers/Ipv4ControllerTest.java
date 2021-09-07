package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.exception.AccesInterditException;
import fr.abes.licencesnationales.exception.IpException;
import fr.abes.licencesnationales.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.repository.EventRepository;
import fr.abes.licencesnationales.services.EmailService;
import fr.abes.licencesnationales.services.EtablissementService;
import fr.abes.licencesnationales.services.IpService;
import fr.abes.licencesnationales.web.dto.ip.Ipv4AjouteeDto;
import fr.abes.licencesnationales.web.dto.ip.Ipv4ModifieeDto;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Slf4j
////////////////////////type ip = IPV4 type acces = ip///////////////////////////////
public class Ipv4ControllerTest extends LicencesNationalesAPIApplicationTests {


    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private FiltrerAccesServices filtrerAccesServices;

    @MockBean
    private IpService ipService;

    private Validator validator;

    @MockBean
    private EmailService emailService;

    private static ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void setUp() throws SirenIntrouvableException, AccesInterditException, IpException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        //Mockito.doNothing().when(ipService).checkDoublonIpAjouteeDto(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());
        EtablissementEntity etablissementEntity = new EtablissementEntity();
        etablissementEntity.setName("testEtab");
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(etablissementEntity);
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    }


    public void ipv4Regex(String ipv4, boolean validates) throws NoSuchFieldException {
        Field field = Ipv4AjouteeDto.class.getDeclaredField("ip");
        javax.validation.constraints.Pattern[] annotations = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        assertEquals(ipv4.matches(annotations[0].regexp()),validates);
    }
    //on met des lettres au lieu d'une ipv4
    @Test
    public void testInvalidIpv4Pattern1() throws NoSuchFieldException {
        ipv4Regex("abcdefgABCDEFG", false);
    }
    //on met une ipv6 au lieu d'une ipv4
    @Test
    public void testInvalidIpv4Pattern2() throws NoSuchFieldException {
        ipv4Regex("5800:10C3:E3C3:F1AA:48E3:D923:D494:AAFF", false);
    }
    //on met une plage ipv4 au lieu d'une ipv4
    @Test
    public void testInvalidIpv4Pattern3() throws NoSuchFieldException {
        ipv4Regex("192.168.20-5.15-2", false);
    }
    //on met une plage ipv6 au lieu d'une ipv4
    @Test
    public void testInvalidIpv4Pattern4() throws NoSuchFieldException {
        ipv4Regex("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF", false);
    }
    //on ne remplit pas le champ ipv4
    @Test
    public void testEmptyIpv4FailValidation() throws NoSuchFieldException {
        ipv4Regex("", false);
    }


    //autre façon de tester
    @Test
    @DisplayName("invalid Pattern Ipv4 Fail Validation")
    public void invalidPatternIpv4FailValidation() {

        Ipv4AjouteeDto ipv4 = new Ipv4AjouteeDto();
        ipv4.setSiren("123456789");
        ipv4.setTypeAcces("ip");
        ipv4.setTypeIp("IPV4");
        ipv4.setIp("mmmmmmmmmm");
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
        ipv4.setIp("192.168.10.2");
        ipv4.setCommentaires("comm");
        Set<ConstraintViolation<Ipv4AjouteeDto>> violations = validator.validate(ipv4);
        assertTrue(violations.isEmpty());
    }

    ////////////////////////////////////ajout ipv4//////////////////////////////////////////////////
    @Test
    @DisplayName("test Etab ajout IPV4 succes")
    @WithMockUser
    public void testEtabAjoutIPV4Succes() throws Exception {

        Ipv4AjouteeDto dto = new Ipv4AjouteeDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20.6");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    //ce test peut apparaitre mauvais mais est bon : c'est l'ordre des arguments du debugMessage qui est aléatoire...
    @Test
    @DisplayName("test Etab ajout IPV4 failed")
    @WithMockUser
    public void testEtabAjoutIPV4Failed() throws Exception {

        Ipv4AjouteeDto dto = new Ipv4AjouteeDto();
        //le traitement ne sera pas bloqué car le siren n'est pas obligatoire dans le dto puisqu'il est récupéré via le token
        //cf : getSirenFromSecurityContextUser()
        dto.setSiren(null);
        dto.setIp(null);
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces(null);
        dto.setTypeIp(null);
        this.mockMvc.perform(post("/v1/ln/ip/ajoutIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : L'IP est obligatoire, Le type acces est obligatoire, Le type ip est obligatoire, "));

    }

    @Test
    @DisplayName("test admin ajout IPV4 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminAjoutIPV4Succes() throws Exception {
        Ipv4AjouteeDto dto = new Ipv4AjouteeDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20.6");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("test admin ajout IPV4 failed")
    @WithMockUser // on ne precise pas role admin
    public void testAdminAjoutIPV4Failed() throws Exception {

        Ipv4AjouteeDto dto = new Ipv4AjouteeDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20.6");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    //////////////////////////////////////////modif ipv4/////////////////////////////////////////////
    @Test
    @DisplayName("test Etab modifier IPV4 succes")
    @WithMockUser
    public void testEtabModifierIPV4Succes() throws Exception {

        Ipv4ModifieeDto dto = new Ipv4ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20.6");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/modifIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Etab modifier IPV4 failed")
    @WithMockUser
    public void testEtabModifierIPV4Failed() throws Exception {

        Ipv4ModifieeDto dto = new Ipv4ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20-1.6-2");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/modifIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : L'IP fournie n'est pas valide, "));

    }


    @Test
    @DisplayName("test Admin modifier IPV4 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminModifierIPV4Succes() throws Exception {

        Ipv4ModifieeDto dto = new Ipv4ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20.6");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Admin modifier IPV4 failed")
    @WithMockUser //on ne precise pas le role admin
    public void testAdminModifierIPV4Failed() throws Exception {

        Ipv4ModifieeDto dto = new Ipv4ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20.6");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }




}
