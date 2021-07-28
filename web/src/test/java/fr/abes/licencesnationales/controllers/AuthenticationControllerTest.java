package fr.abes.licencesnationales.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.MockUserUtil;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.security.payload.request.LoginRequest;
import fr.abes.licencesnationales.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.security.services.impl.UserDetailsServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class AuthenticationControllerTest  extends LicencesNationalesAPIApplicationTests {
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsServiceImpl service;

    EtablissementEntity user = MockUserUtil.getMockUser();

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() throws DonneeIncoherenteBddException {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("123456789", "secret"));
        Mockito.when(service.loadUserByUsername(Mockito.anyString())).thenReturn(UserDetailsImpl.build(user));
    }

    @Test
    public void doLoginGet()  throws Exception {
        mockMvc.perform(get("/v1/login")).andExpect(status().isMethodNotAllowed());
    }

    @DisplayName("test authentification réussie")
    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("passwordtest");
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

    @DisplayName("test authentification mauvais login / mdp")
    @Test
    public void testLoginFailed() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("00000000");
        request.setLogin("000000000");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenThrow(new UsernameNotFoundException("000000000"));

        String json = mapper.writeValueAsString(request);
        this.mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
