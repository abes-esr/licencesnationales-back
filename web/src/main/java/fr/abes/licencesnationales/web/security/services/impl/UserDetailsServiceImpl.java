package fr.abes.licencesnationales.web.security.services.impl;


import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.web.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.services.EtablissementService;
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

	public UserDetailsServiceImpl(EtablissementService etablissementService) {/**/
		this.etablissementService = etablissementService;
	}

	@SneakyThrows
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		log.info("UserDetailsServiceImpl début");
		log.info("siren = " + username);
		EtablissementEntity user = etablissementService.getFirstBySiren(username);
		log.info("user = " + user);
		if(user == null) {
			log.info("UsernameNotFoundException");
			throw new UsernameNotFoundException(username);
		}

		log.info("UserDetailsServiceImpl fin");
		return UserDetailsImpl.build(user);
	}
	@Transactional
	public UserDetails loadUser(EtablissementEntity user) throws UsernameNotFoundException, DonneeIncoherenteBddException {
		log.info("UserDetailsServiceImpl début loadUser");
		log.info("user = " + user);
		log.info("UserDetailsServiceImpl fin");
		return UserDetailsImpl.build(user);
	}

}
