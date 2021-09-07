package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.dto.ip.IpSupprimeeDto;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.exception.AccesInterditException;
import fr.abes.licencesnationales.exception.IpException;
import fr.abes.licencesnationales.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.repository.EventRepository;
import fr.abes.licencesnationales.services.EmailService;
import fr.abes.licencesnationales.services.EtablissementService;
import fr.abes.licencesnationales.services.IpService;
import fr.abes.licencesnationales.web.dto.ip.Ipv6AjouteeDto;
import fr.abes.licencesnationales.web.dto.ip.Ipv6ModifieeDto;
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
////////////////////////type ip = IPV6 type acces = ip///////////////////////////////
public class Ipv6ControllerTest extends LicencesNationalesAPIApplicationTests {


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
        Mockito.doNothing().when(ipService).checkDoublonIpAjouteeDto(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new EventEntity());
        EtablissementEntity etablissementEntity = new EtablissementEntity();
        etablissementEntity.setName("testEtab");
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(etablissementEntity);
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    }


    public void ipv6Regex(String ipv6, boolean validates) throws NoSuchFieldException {
        Field field = Ipv6AjouteeDto.class.getDeclaredField("ip");
        javax.validation.constraints.Pattern[] annotations = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        assertEquals(ipv6.matches(annotations[0].regexp()),validates);
    }
    //on met des lettres au lieu d'une ipv6
    @Test
    public void testInvalidIpv6Pattern1() throws NoSuchFieldException {
        ipv6Regex("abcdefgABCDEFG", false);
    }
    //on met une ipv4 au lieu d'une ipv6
    @Test
    public void testInvalidIpv6Pattern2() throws NoSuchFieldException {
        ipv6Regex("192.168.20.20", false);
    }
    //on met une plage ipv4 au lieu d'une ipv6
    @Test
    public void testInvalidIpv6Pattern3() throws NoSuchFieldException {
        ipv6Regex("192.168.20-5.15-2", false);
    }
    //on met une plage ipv6 au lieu d'une ipv6
    @Test
    public void testInvalidIpv6Pattern4() throws NoSuchFieldException {
        ipv6Regex("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF", false);
    }
    //on ne remplit pas le champ ipv6
    @Test
    public void testEmptyIpv6FailValidation() throws NoSuchFieldException {
        ipv6Regex("", false);
    }


    //autre façon de tester
    @Test
    @DisplayName("invalid Pattern Ipv6 Fail Validation")
    public void invalidPatternIpv6FailValidation() {

        Ipv6AjouteeDto ipv6 = new Ipv6AjouteeDto();
        ipv6.setSiren("123456789");
        ipv6.setTypeAcces("ip");
        ipv6.setTypeIp("IPV6");
        ipv6.setIp("mmmmmmmmmm");
        ipv6.setCommentaires("comm");
        Set<ConstraintViolation<Ipv6AjouteeDto>> violations = validator.validate(ipv6);
        assertFalse(violations.isEmpty());
    }
    @Test
    @DisplayName("validPatternIpv6Validation")
    public void validPatternIpv6Validation() {

        Ipv6AjouteeDto ipv6 = new Ipv6AjouteeDto();
        ipv6.setSiren("123456789");
        ipv6.setTypeAcces("ip");
        ipv6.setTypeIp("IPV6");
        ipv6.setIp("2001:470:1f14:10b9:0000:0000:0000:2");
        ipv6.setCommentaires("comm");
        Set<ConstraintViolation<Ipv6AjouteeDto>> violations = validator.validate(ipv6);
        assertTrue(violations.isEmpty());
    }

    ////////////////////////////////////ajout ipv6//////////////////////////////////////////////////
    @Test
    @DisplayName("test Etab ajout IPV6 succes")
    @WithMockUser
    public void testEtabAjoutIPV6Succes() throws Exception {

        Ipv6AjouteeDto dto = new Ipv6AjouteeDto();
        dto.setSiren("123456789");
        dto.setIp("2001:470:1f14:10b9:0000:0000:0000:2");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/ajoutIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    //ce test peut apparaitre mauvais mais est bon : c'est l'ordre des arguments du debugMessage qui est aléatoire...
    @Test
    @DisplayName("test Etab ajout IPV6 failed")
    @WithMockUser
    public void testEtabAjoutIPV6Failed() throws Exception {

        Ipv6AjouteeDto dto = new Ipv6AjouteeDto();
        //le traitement ne sera pas bloqué car le siren n'est pas obligatoire dans le dto puisqu'il est récupéré via le token
        //cf : getSirenFromSecurityContextUser()
        dto.setSiren(null);
        dto.setIp(null);
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces(null);
        dto.setTypeIp(null);
        this.mockMvc.perform(post("/v1/ln/ip/ajoutIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : L'IP est obligatoire, Le type acces est obligatoire, Le type ip est obligatoire, "));

    }

    @Test
    @DisplayName("test admin ajout IPV6 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminAjoutIPV6Succes() throws Exception {

        Ipv6AjouteeDto dto = new Ipv6AjouteeDto();
        dto.setSiren("123456789");
        dto.setIp("2001:470:1f14:10b9:0000:0000:0000:2");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("test admin ajout IPV6 failed")
    @WithMockUser // on ne precise pas role admin
    public void testAdminAjoutIPV6Failed() throws Exception {

        Ipv6AjouteeDto dto = new Ipv6AjouteeDto();
        dto.setSiren("123456789");
        dto.setIp("2001:470:1f14:10b9:0000:0000:0000:2");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminAjoutIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    //////////////////////////////////////////modif ipv6/////////////////////////////////////////////
    @Test
    @DisplayName("test Etab modifier IPV6 succes")
    @WithMockUser
    public void testEtabModifierIPV6Succes() throws Exception {

        Ipv6ModifieeDto dto = new Ipv6ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("2001:470:1f14:10b9:0000:0000:0000:3");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/modifIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Etab modifier IPV6 failed")
    @WithMockUser
    public void testEtabModifierIPV6Failed() throws Exception {

        Ipv6ModifieeDto dto = new Ipv6ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("2001:470:1f14:10b9:0000:0000:0000-000:3-2");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/modifIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : L'IP fournie n'est pas valide, "));
    }

    @Test
    @DisplayName("test Admin modifier IPV6 succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminModifierIPV6Succes() throws Exception {

        Ipv6ModifieeDto dto = new Ipv6ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("2001:470:1f14:10b9:0000:0000:0000:3");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Admin modifier IPV6 failed")
    @WithMockUser //on ne precise pas le role admin
    public void testAdminModifierIPV6Failed() throws Exception {

        Ipv6ModifieeDto dto = new Ipv6ModifieeDto();
        dto.setSiren("123456789");
        dto.setIp("2001:470:1f14:10b9:0000:0000:0000:3");
        dto.setCommentaires("Cette ip etc");
        dto.setTypeAcces("ip");
        dto.setTypeIp("IPV6");
        this.mockMvc.perform(post("/v1/ln/ip/adminModifIpV6")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    /////////////////////////////////////////suppression ip//////////////////////////////
    @Test
    @DisplayName("test Etab supprimer IP succes")
    @WithMockUser
    public void testEtabSuppIPSucces() throws Exception {

        IpSupprimeeDto dto = new IpSupprimeeDto();
        dto.setSiren("123456789");
        dto.setId(Long.valueOf("1"));
        this.mockMvc.perform(delete("/v1/ln/ip/supprime")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Etab supprimer IP failed")
    @WithMockUser
    public void testEtabSuppIPFailed() throws Exception {

        IpSupprimeeDto dto = new IpSupprimeeDto();
        dto.setSiren("123456789");
        dto.setId(null);
        this.mockMvc.perform(delete("/v1/ln/ip/supprime")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields :  L'id est obligatoire, "));
    }

    @Test
    @DisplayName("test Admin supprimer IP succes")
    @WithMockUser(authorities = {"admin"})
    public void testAdminSuppIPSucces() throws Exception {

        IpSupprimeeDto dto = new IpSupprimeeDto();
        dto.setSiren("123456789");
        dto.setId(Long.valueOf("1"));
        this.mockMvc.perform(delete("/v1/ln/ip/supprimeByAdmin")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Admin supprimer IP failed")
    @WithMockUser
    public void testAdminSuppIPFailed() throws Exception {

        IpSupprimeeDto dto = new IpSupprimeeDto();
        dto.setSiren("123456789");
        dto.setId(Long.valueOf("1"));
        this.mockMvc.perform(delete("/v1/ln/ip/supprimeByAdmin")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test Admin supprimer IP failed 2")
    @WithMockUser(authorities = {"admin"})
    public void testAdminSuppIPFailed2() throws Exception {

        IpSupprimeeDto dto = new IpSupprimeeDto();
        dto.setSiren(null); //le siren n'est pas obligatoire vu que en situation etab on va le chercher dans le token
        //par contre en admin il n'est pas obligatoire alors que est un param indispensable puisqu'on transmet le siren de l'etab dont on veut changer l'ip
        //donc à voir si il ne faut pas retifier ça, en envoyant malgré tout depuis le front le siren de l'etab qui souhaite supp/modi/ajouter une ip
        //et faire malgré tout derrière la verif dans le token sans forcément recup le siren dans le token vu qu'on l'aura déjà en param de la request
        dto.setId(null);
        this.mockMvc.perform(delete("/v1/ln/ip/supprimeByAdmin")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Incorrect fields :  L'id est obligatoire, "));
    }

}
