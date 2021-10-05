package fr.abes.licencesnationales.web.security.services;


import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class FiltrerAccesServices {
    @Autowired
    EtablissementService service;

    @Autowired
    private JwtTokenProvider tokenProvider;


    public void autoriserServicesParSiren(String sirenFromController) throws SirenIntrouvableException, AccesInterditException {
        String sirenFromJwt = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sirenFromSecurityContextUser = userDetails.getUsername();
        //le test d'acceptance :
        //soit on compare sirenFromController avec sirenFromJwtContenuDansLaRequest
        //soit on compare sirenFromController avec sirenFromSecurityContextUser => là on remonte un peu plus haut que le token


        if (!sirenFromSecurityContextUser.equals(sirenFromController) && !userDetails.getRole().equals("admin")) {
            log.error("Acces interdit");
            throw new AccesInterditException("Acces interdit");
        }
        if (!service.existeSiren(sirenFromJwt)) {
            log.error("Siren absent de la base");
            throw new SirenIntrouvableException("Siren absent de la base");
        }
    }

    public String getSirenFromSecurityContextUser() throws SirenIntrouvableException, AccesInterditException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sirenFromSecurityContextUser = userDetails.getUsername();
        log.debug("sirenFromSecurityContextUser = " + sirenFromSecurityContextUser);
        if (sirenFromSecurityContextUser.equals("") || sirenFromSecurityContextUser == null) {
            log.error("Acces interdit");
            throw new AccesInterditException("Acces interdit");
        }
        boolean existeSiren = service.existeSiren(sirenFromSecurityContextUser);
        log.debug("existeSiren = " + existeSiren);
        if (!existeSiren) {
            log.error("Siren absent de la base");
            throw new SirenIntrouvableException("Siren absent de la base");
        }
        return sirenFromSecurityContextUser;
    }

    public String getRoleFromSecurityContextUser() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();
    }

}


