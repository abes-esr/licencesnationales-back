package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.MockUserUtil;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.web.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.web.security.payload.request.LoginRequest;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class AuthenticationControllerTest
{

    /** pour faire marcher le test,
     *  dans le MockUserUtil mettre "secret" au lieu de passwordEncoder.encode("OldPass1Test&")
     */



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    Authentication authentication;

    @MockBean
    private AuthenticationManager authenticationManager;

    private static ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void init() throws DonneeIncoherenteBddException {
        EtablissementEntity localUser = new MockUserUtil(passwordEncoder).getMockUser();
        Mockito.when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(localUser.getSiren(), localUser.getContact().getMotDePasse()))).thenReturn(authentication);

        //Pour éviter la base de donnée :
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(localUser.getContact().getRole()));
        UserDetailsImpl userDetails = new UserDetailsImpl(Long.valueOf(localUser.getId()),localUser.getSiren(), localUser.getName(), localUser.getContact().getMotDePasse(), localUser.getContact().getMail(), authorities,false);

        //quand on fait :
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        //c'est comme si on faisait :
        //Mockito.when(service.loadUserByUsername(localUser.getSiren())).thenReturn(UserDetailsImpl.build(localUser));

        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    }


    @Test
    public void doLoginGet()  throws Exception {
        mockMvc.perform(get("/v1/login")).andExpect(status().isMethodNotAllowed());
    }


    @Test
    @DisplayName("test authentification réussie")
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("secret");
        request.setLogin("123456789");

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("test authentification mauvais login / mdp")
    //derrière la NestedServletException il y a un NullPointerException ==> qd on charge le userDetailImpl
    public void testLoginFailed() throws Exception {
        assertThrows(NestedServletException.class,()->{ // pourquoi on obtient pas plutôt une BadCredentialsException?
            LoginRequest request = new LoginRequest();
            request.setPassword("secret");
            request.setLogin("123654789");

            this.mockMvc.perform(post("/v1/login")
                    .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)));
        });
    }

    @Test
    @DisplayName("test Validator Pattern marche")
    //verif que @NotBlank et @Pattern du LoginRequest marche bien via @Valid RequestBody
    public void testValidatorMarche() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setPassword("");
            request.setLogin("");

            this.mockMvc.perform(post("/v1/login")
                    .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("The credentials are not valid"))
                    .andExpect(jsonPath("$.debugMessage").value("Incorrect fields : Le SIREN doit contenir 9 chiffres, SIREN obligatoire (login), Mot de passe obligatoire (password), "));

    }


    @Test
    @DisplayName("test Validator Pattern marche 2 ")
    //cas siren 3 chiffres au lieu de 9 ==> on passe dans le ExceptionControllerHandler handleMethodArgumentNotValid package web
    public void testValidatorTheCredentialsAreNotValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("secret");
        request.setLogin("123"); // ou "mmm" etc

        this.mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The credentials are not valid"));
    }
}
