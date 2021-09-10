package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.web.dto.ip.PlageIpv6AjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.PlageIpv6ModifieeWebDto;
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
////////////////////////type ip = IPV6 type acces = plage///////////////////////////////
public class PlageIpv6ControllerTest extends LicencesNationalesAPIApplicationTests {


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
       // Mockito.doNothing().when(ipService).checkDoublonIpAjouteeDto(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());
        EtablissementEntity etablissementEntity = new EtablissementEntity();
        etablissementEntity.setName("testEtab");
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(etablissementEntity);
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    }


    public void plageIpv6Regex(String plageIpv6, boolean validates) throws NoSuchFieldException {
        Field field = PlageIpv6AjouteeWebDto.class.getDeclaredField("ip");
        javax.validation.constraints.Pattern[] annotations = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        assertEquals(plageIpv6.matches(annotations[0].regexp()),validates);
    }
    //on met des lettres au lieu d'une plage Ipv6
    @Test
    public void testInvalidPlageIpv6Pattern1() throws NoSuchFieldException {
        plageIpv6Regex("abcdefgABCDEFG", false);
    }
    //on met une ipv4 au lieu d'une plage Ipv6
    @Test
    public void testInvalidPlageIpv6Pattern2() throws NoSuchFieldException {
        plageIpv6Regex("192.168.20.15", false);
    }
    //on met une ipv6 au lieu d'une plage Ipv6
    @Test
    public void testInvalidPlageIpv6Pattern3() throws NoSuchFieldException {
        plageIpv6Regex("5800:10C3:E3C3:F1AA:48E3:D923:D494:AAFF", false);
    }
    //on met une plage ipv4 au lieu d'une plage Ipv6
    @Test
    public void testInvalidPlageIpv6Pattern4() throws NoSuchFieldException {
        plageIpv6Regex("192.168.10-6.6-2", false);
    }
    //on ne remplit pas le champ plageIpv6
    @Test
    public void testEmptyPlageIpv6FailValidation() throws NoSuchFieldException {
        plageIpv6Regex("", false);
    }


    //autre façon de tester
    @Test
    @DisplayName("invalid Pattern PlageIpv6 Fail Validation")
    public void invalidPatternPlageIpv6FailValidation() {

        PlageIpv6AjouteeWebDto plageIpv6 = new PlageIpv6AjouteeWebDto();
        plageIpv6.setSiren("123456789");
        plageIpv6.setTypeAcces("plage");
        plageIpv6.setTypeIp("IPV6");
        plageIpv6.setIp("mmmmmmmmmm");
        plageIpv6.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv6AjouteeWebDto>> violations = validator.validate(plageIpv6);
        assertFalse(violations.isEmpty());
    }
    @Test
    @DisplayName("validPatternPlageIpv6Validation")
    public void validPatternPlageIpv6Validation() {

        PlageIpv6AjouteeWebDto plageIpv6 = new PlageIpv6AjouteeWebDto();
        plageIpv6.setSiren("123456789");
        plageIpv6.setTypeAcces("plage");
        plageIpv6.setTypeIp("IPV6");
        plageIpv6.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF");
        plageIpv6.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv6AjouteeWebDto>> violations = validator.validate(plageIpv6);
        assertTrue(violations.isEmpty());
    }

    ////////////////////////////////////ajout plageIpv6//////////////////////////////////////////////////
    @Test
    @DisplayName("test Etab ajout plage IPV6 succes")
    @WithMockUser
    public void testEtabAjoutPlageIPV6Succes() throws Exception {

        PlageIpv6AjouteeWebDto dto = new PlageIpv6AjouteeWebDto();
        dto.setSiren("123456789");
        dto.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    //ce test peut apparaitre mauvais mais est bon : c'est l'ordre des arguments du debugMessage qui est aléatoire...
    @Test
    @DisplayName("test Etab ajout plage IPV6 failed")
    @WithMockUser
    public void testEtabAjoutPlageIPV6Failed() throws Exception {

        PlageIpv6AjouteeWebDto dto = new PlageIpv6AjouteeWebDto();
        //le traitement ne sera pas bloqué car le siren n'est pas obligatoire dans le dto puisqu'il est récupéré via le token
        //cf : getSirenFromSecurityContextUser()
        dto.setSiren(null);
        dto.setIp(null);
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces(null);
        dto.setTypeIp(null);
        this.mockMvc.perform(post("/v1/ln/ip/ajoutPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : La plage d'Ips est obligatoire, "));

    }

    @Test
    @DisplayName("test admin ajout plage IPV6 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminAjoutPlageIPV6Succes() throws Exception {

        PlageIpv6AjouteeWebDto dto = new PlageIpv6AjouteeWebDto();
        dto.setSiren("123456789");
        dto.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("test admin ajout plage IPV6 failed")
    @WithMockUser // on ne precise pas role admin
    public void testAdminAjoutPlageIPV6Failed() throws Exception {

        PlageIpv6AjouteeWebDto dto = new PlageIpv6AjouteeWebDto();
        dto.setSiren("123456789");
        dto.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    //////////////////////////////////////////modif plageIpv6/////////////////////////////////////////////
    @Test
    @DisplayName("test Etab modifier Plage IPV6 succes")
    @WithMockUser
    public void testEtabModifierPlageIPV6Succes() throws Exception {

        PlageIpv6ModifieeWebDto dto = new PlageIpv6ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/modifPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Etab modifier Plage IPV6 failed")
    @WithMockUser
    public void testEtabModifierPlageIPV6Failed() throws Exception {

        PlageIpv6ModifieeWebDto dto = new PlageIpv6ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494:AAFF");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/modifPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : La plage d'Ips fournie n'est pas valide, "));

    }


    @Test
    @DisplayName("test Admin modifier Plage IPV6 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminModifierPlageIPV6Succes() throws Exception {

        PlageIpv6ModifieeWebDto dto = new PlageIpv6ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Admin modifier plage IPV6 failed")
    @WithMockUser //on ne precise pas le role admin
    public void testAdminModifierPlageIPV6Failed() throws Exception {

        PlageIpv6ModifieeWebDto dto = new PlageIpv6ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifPlageIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
