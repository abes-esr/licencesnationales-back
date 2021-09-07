package fr.abes.licencesnationales.web.security.services;


import fr.abes.licencesnationales.exception.AccesInterditException;
import fr.abes.licencesnationales.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.services.EtablissementService;
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

        //Services EtablissementController

        public void autoriserServicesParSiren(String sirenFromController) throws SirenIntrouvableException, AccesInterditException {
            log.info("Début autoriserServicesParSiren");
            log.info("sirenFromController = " + sirenFromController);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            log.info("jwt = " + tokenProvider.getJwtFromRequest(attr.getRequest()));
            String jwtContenuDansLaRequest = tokenProvider.getJwtFromRequest(attr.getRequest());
            String sirenFromJwt = tokenProvider.getSirenFromJwtToken(jwtContenuDansLaRequest);
            log.info("siren = " + sirenFromJwt);
            log.info("userDetails = " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String sirenFromSecurityContextUser = userDetails.getUsername();
            log.info("sirenFromSecurityContextUser = " + sirenFromSecurityContextUser);
            log.info("type 1 = " + sirenFromSecurityContextUser.getClass().getSimpleName());
            log.info("type 2 = " + sirenFromController.getClass().getSimpleName());

            //le test d'acceptance :
            //soit on compare sirenFromController avec sirenFromJwtContenuDansLaRequest
            //soit on compare sirenFromController avec sirenFromSecurityContextUser => là on remonte un peu plus haut que le token


            if(!sirenFromSecurityContextUser.equals(sirenFromController)){
                log.error("Acces interdit");
                throw new AccesInterditException("Acces interdit");
            }
            boolean existeSiren = service.existeSiren(sirenFromJwt); // ou existeSiren(sirenFromSecurityContextUser)
            log.info("existeSiren = "+ existeSiren);
            if (!existeSiren) {
                log.error("Siren absent de la base");
                throw new SirenIntrouvableException("Siren absent de la base");
            }
        }

    public String getSirenFromSecurityContextUser() throws SirenIntrouvableException, AccesInterditException{
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sirenFromSecurityContextUser = userDetails.getUsername();
        log.info("sirenFromSecurityContextUser = " + sirenFromSecurityContextUser);
        if(sirenFromSecurityContextUser.equals("") || sirenFromSecurityContextUser==null){
            log.error("Acces interdit");
            throw new AccesInterditException("Acces interdit");
        }
        boolean existeSiren = service.existeSiren(sirenFromSecurityContextUser);
        log.info("existeSiren = "+ existeSiren);
        if (!existeSiren) {
            log.error("Siren absent de la base");
            throw new SirenIntrouvableException("Siren absent de la base");
        }
        return sirenFromSecurityContextUser;
    }

}


