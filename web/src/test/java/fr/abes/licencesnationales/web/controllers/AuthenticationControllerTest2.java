package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.MockUserUtil;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.web.security.payload.request.LoginRequest;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class AuthenticationControllerTest2 {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    Authentication authentication;

    @MockBean
    EtablissementRepository etablissementRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    EtablissementService etablissementService;

    @MockBean
    private UserDetailsServiceImpl service;

    @MockBean
    private EtablissementEntity user;

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() throws DonneeIncoherenteBddException {
        EtablissementEntity localUser = new MockUserUtil(passwordEncoder).getMockUser();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(localUser.getSiren(), localUser.getContact().getMotDePasse()));
        Mockito.when(etablissementService.getFirstBySiren(Mockito.anyString())).thenReturn(localUser);
        //le pb est ici : service.loadUserByUsername(parceque fait appel à EtablissementService qui est à null
        Mockito.when(service.loadUserByUsername(localUser.getSiren())).thenReturn(UserDetailsImpl.build(localUser));
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    }


    /*@BeforeEach
    public void init() throws DonneeIncoherenteBddException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        user = new MockUserUtil(passwordEncoder).getMockUser();
        //SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("123456789", "secret"));
        Mockito.when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("123456789", "secret"))).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(service.loadUserByUsername((String) authentication.getPrincipal())).thenReturn(UserDetailsImpl.build(user));
    }*/

    @Test
    public void doLoginGet()  throws Exception {
        mockMvc.perform(get("/v1/login")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("123456789");
        loginRequest.setPassword("secret");
        String json = mapper.writeValueAsString(loginRequest);
        this.mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("test authentification réussie")
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("secret");
        request.setLogin("123456789");

        UserDetailsImpl localUser = UserDetailsImpl.build(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(localUser, null);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("test authentification réussie 2")
    public void testLoginSuccess2() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("secret");
        request.setLogin("123456789");

       /* Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);*/

        //UserDetailsImpl localUser = UserDetailsImpl.build(user);
        //UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword());
        //authenticationToken.setAuthenticated(true);
        //Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        //UserDetailsImpl localUser = UserDetailsImpl.build(user);
        //UserDetailsImpl localUser = UserDetailsImpl.build((UserDetailsImpl) authentication.getPrincipal());

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }


    @Test
    @DisplayName("test authentification mauvais login / mdp")
    public void testLoginFailed() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("00000000");
        request.setLogin("000000000");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenThrow(new UsernameNotFoundException("000000000"));

        this.mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credentials not valid"));
    }
}
