package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.services.ArbreService;
import fr.abes.licencesnationales.core.services.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/v1")
public class ServiceController {
    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

    @Value("${siren.url}")
    private String sirenUrl;

    @Value("${siren.token}")
    private String sirenToken;

    @GetMapping(value = "/resetEtablissement")
    public void resetEtablissement() throws IOException {
        resetService.resetEtablissement();
    }

    @GetMapping(value = "/arbre")
    public String arbre() throws ParseException, IOException {
        return arbreService.genereArbre();
    }

    @GetMapping(value = "/applicationVersion")
    public String version() {
        return this.getClass().getPackage().getImplementationVersion();
    }

    @GetMapping(value="/siren/{siren}")
    public ResponseEntity<String> siren(@PathVariable String siren) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-INSEE-Api-Key-Integration", sirenToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =  restTemplate.exchange(sirenUrl + siren, HttpMethod.GET, entity, String.class);

        return new ResponseEntity<>(
                response.getBody(),
                response.getStatusCode());
    }

}
