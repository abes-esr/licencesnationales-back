package fr.abes.lnevent.security.services;

import fr.abes.lnevent.exception.AccesInterditException;
import fr.abes.lnevent.exception.SirenIntrouvableException;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
            log.debug("Début autoriserSuppressionEtabParSiren");
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            log.info("token = " + attr.getRequest().getParameter("token"));
            String jwt = tokenProvider.getJwtFromRequest(attr.getRequest());
            String siren = tokenProvider.getSirenFromJwtToken(jwt);
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


