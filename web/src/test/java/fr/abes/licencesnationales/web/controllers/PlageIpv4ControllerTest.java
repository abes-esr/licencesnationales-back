package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEventEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.web.dto.ip.PlageIpv4AjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.PlageIpv4ModifieeWebDto;
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
////////////////////////type ip = IPV4 type acces = plage///////////////////////////////
public class PlageIpv4ControllerTest extends LicencesNationalesAPIApplicationTests {


    @MockBean
    private IpEventRepository eventRepository;

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
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new IpEventEntity());
        EtablissementEntity etablissementEntity = new EtablissementEntity();
        etablissementEntity.setName("testEtab");
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(etablissementEntity);
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    }


    public void plageIpv4Regex(String plageIpv4, boolean validates) throws NoSuchFieldException {
        Field field = PlageIpv4AjouteeWebDto.class.getDeclaredField("ip");
        javax.validation.constraints.Pattern[] annotations = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        assertEquals(plageIpv4.matches(annotations[0].regexp()),validates);
    }


    //autre façon de tester
    @Test
    @DisplayName("invalid Pattern PlageIpv4 Fail Validation")
    public void invalidPatternPlageIpv4FailValidation() {

        PlageIpv4AjouteeWebDto plageIpv4 = new PlageIpv4AjouteeWebDto();
        plageIpv4.setSiren("123456789");
        plageIpv4.setTypeAcces("plage");
        plageIpv4.setTypeIp("IPV4");
        plageIpv4.setIp("mmmmmmmmmm");
        plageIpv4.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv4AjouteeWebDto>> violations = validator.validate(plageIpv4);
        assertFalse(violations.isEmpty());
    }
    @Test
    @DisplayName("validPatternPlageIpv4Validation")
    public void validPatternPlageIpv4Validation() {

        PlageIpv4AjouteeWebDto plageIpv4 = new PlageIpv4AjouteeWebDto();
        plageIpv4.setSiren("123456789");
        plageIpv4.setTypeAcces("plage");
        plageIpv4.setTypeIp("IPV4");
        plageIpv4.setIp("192.168.10-2.2-1");
        plageIpv4.setCommentaires("comm");
        Set<ConstraintViolation<PlageIpv4AjouteeWebDto>> violations = validator.validate(plageIpv4);
        assertTrue(violations.isEmpty());
    }

    ////////////////////////////////////ajout plageIpv4//////////////////////////////////////////////////
    @Test
    @DisplayName("test Etab ajout plage IPV4 succes")
    @WithMockUser
    public void testEtabAjoutPlageIPV4Succes() throws Exception {

        PlageIpv4AjouteeWebDto dto = new PlageIpv4AjouteeWebDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20-2.6-1");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    //ce test peut apparaitre mauvais mais est bon : c'est l'ordre des arguments du debugMessage qui est aléatoire...
    @Test
    @DisplayName("test Etab ajout plage IPV4 failed")
    @WithMockUser
    public void testEtabAjoutPlageIPV4Failed() throws Exception {

        PlageIpv4AjouteeWebDto dto = new PlageIpv4AjouteeWebDto();
        //le traitement ne sera pas bloqué car le siren n'est pas obligatoire dans le dto puisqu'il est récupéré via le token
        //cf : getSirenFromSecurityContextUser()
        dto.setSiren(null);
        dto.setIp(null);
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces(null);
        dto.setTypeIp(null);
        this.mockMvc.perform(post("/v1/ln/ip/ajoutPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : La plage d'Ips est obligatoire, "));

    }

    @Test
    @DisplayName("test admin ajout plage IPV4 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminAjoutPlageIPV4Succes() throws Exception {

        PlageIpv4AjouteeWebDto dto = new PlageIpv4AjouteeWebDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20-6.6-2");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("test admin ajout plage IPV4 failed")
    @WithMockUser // on ne precise pas role admin
    public void testAdminAjoutPlageIPV4Failed() throws Exception {

        PlageIpv4AjouteeWebDto dto = new PlageIpv4AjouteeWebDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20-6.6-2");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    //////////////////////////////////////////modif plageIpv4/////////////////////////////////////////////
    @Test
    @DisplayName("test Etab modifier Plage IPV4 succes")
    @WithMockUser
    public void testEtabModifierPlageIPV4Succes() throws Exception {

        PlageIpv4ModifieeWebDto dto = new PlageIpv4ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20-6.6-2");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/modifPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Etab modifier Plage IPV4 failed")
    @WithMockUser
    public void testEtabModifierPlageIPV4Failed() throws Exception {

        PlageIpv4ModifieeWebDto dto = new PlageIpv4ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20.6");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/modifPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : La plage d'Ips fournie n'est pas valide, "));

    }


    @Test
    @DisplayName("test Admin modifier Plage IPV4 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminModifierPlageIPV4Succes() throws Exception {

        PlageIpv4ModifieeWebDto dto = new PlageIpv4ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20-6.6-2");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Admin modifier plage IPV4 failed")
    @WithMockUser //on ne precise pas le role admin
    public void testAdminModifierPlageIPV4Failed() throws Exception {

        PlageIpv4ModifieeWebDto dto = new PlageIpv4ModifieeWebDto();
        dto.setSiren("123456789");
        dto.setIp("192.168.20-6.6-2");
        dto.setCommentaires("Cette plage ip etc");
        dto.setTypeAcces("plage");
        dto.setTypeIp("IPV4");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifPlageIpV4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
