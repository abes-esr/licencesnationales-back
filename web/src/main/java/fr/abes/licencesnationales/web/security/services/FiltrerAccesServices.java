package fr.abes.licencesnationales.web.security.services;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.web.exception.InvalidTokenException;
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


    public void autoriserServicesParSiren(String sirenFromController) throws SirenIntrouvableException, AccesInterditException, InvalidTokenException {
        String sirenFromJwt = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sirenFromSecurityContextUser = userDetails.getUsername();
        //le test d'acceptance :
        //soit on compare sirenFromController avec sirenFromJwtContenuDansLaRequest
        //soit on compare sirenFromController avec sirenFromSecurityContextUser => là on remonte un peu plus haut que le token


        if (!sirenFromSecurityContextUser.equals(sirenFromController) && !userDetails.getRole().equals("admin")) {
            log.error(Constant.ACCES_INTERDIT);
            throw new AccesInterditException(Constant.ACCES_INTERDIT);
        }
        if (!service.existeSiren(sirenFromJwt)) {
            log.error(Constant.ERROR_SIREN_INTROUVABLE);
            throw new SirenIntrouvableException(Constant.ERROR_SIREN_INTROUVABLE);
        }
    }

    public String getSirenFromSecurityContextUser() throws SirenIntrouvableException, AccesInterditException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sirenFromSecurityContextUser = userDetails.getUsername();
        log.debug("sirenFromSecurityContextUser = " + sirenFromSecurityContextUser);
        if (sirenFromSecurityContextUser.equals("") || sirenFromSecurityContextUser == null) {
            log.error(Constant.ACCES_INTERDIT);
            throw new AccesInterditException(Constant.ACCES_INTERDIT);
        }
        boolean existeSiren = service.existeSiren(sirenFromSecurityContextUser);
        log.debug("existeSiren = " + existeSiren);
        if (!existeSiren) {
            log.error(Constant.ERROR_SIREN_INTROUVABLE);
            throw new SirenIntrouvableException(Constant.ERROR_SIREN_INTROUVABLE);
        }
        return sirenFromSecurityContextUser;
    }

    public String getRoleFromSecurityContextUser() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();
    }

}


