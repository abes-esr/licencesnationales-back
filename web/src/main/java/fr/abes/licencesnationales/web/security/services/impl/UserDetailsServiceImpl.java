package fr.abes.licencesnationales.web.security.services.impl;


import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final EtablissementService etablissementService;

    public UserDetailsServiceImpl(EtablissementService etablissementService) {
        this.etablissementService = etablissementService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EtablissementEntity etab = etablissementService.getFirstBySiren(username);
        if (etab == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserDetailsImpl(etab);
    }

    @Transactional
    public UserDetails loadUser(EtablissementEntity etab) {
        if (etab == null) {
            throw new IllegalArgumentException("L'établissement ne peut pas être nul");
        }
        return new UserDetailsImpl(etab);
    }

}
