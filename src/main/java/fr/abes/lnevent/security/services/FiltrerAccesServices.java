package fr.abes.lnevent.security.services;

import fr.abes.lnevent.exception.AccesInterditException;
import fr.abes.lnevent.exception.SirenIntrouvableException;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.security.jwt.JwtTokenProvider;
import fr.abes.lnevent.security.services.impl.UserDetailsImpl;
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
        EtablissementRepository etablissementRepository;

        @Autowired
        private JwtTokenProvider tokenProvider;

        //Services EtablissementController

        public void autoriserServicesParSiren(String sirenFromController) throws SirenIntrouvableException, AccesInterditException {
            log.info("Début autoriserServicesParSiren");
            log.info("sirenFromController = " + sirenFromController);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            log.info("jwt = " + tokenProvider.getJwtFromRequest(attr.getRequest()));
            String jwt = tokenProvider.getJwtFromRequest(attr.getRequest());
            String siren = tokenProvider.getSirenFromJwtToken(jwt);
            log.info("siren = " + siren);
            log.info("userDetails = " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String sirenFromSecurityContextUser = userDetails.getUsername();
            log.info("sirenFromSecurityContextUser = " + sirenFromSecurityContextUser);

            if(!siren.equals(sirenFromController)){
                log.error("Acces interdit");
                throw new AccesInterditException("Acces interdit");
            }
            boolean existeSiren = etablissementRepository.existeSiren(siren);
            log.info("existeSiren = "+ existeSiren);
            if (!existeSiren) {
                log.error("Siren absent de la base");
                throw new SirenIntrouvableException("Siren absent de la base");
            }
        }

        //Services IpController

}


