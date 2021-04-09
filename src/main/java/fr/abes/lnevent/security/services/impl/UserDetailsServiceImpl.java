package fr.abes.lnevent.security.services.impl;

import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	EtablissementRepository etablissementRepository;


	@Override
	@Transactional
	public UserDetails loadUserByUsername(String siren) throws UsernameNotFoundException {
		log.info("UserDetailsServiceImpl début");
		log.info("siren = " + siren);
		EtablissementEntity user = etablissementRepository.getFirstBySiren(siren);
		log.info("user = " + user);
		if(user == null) {
			log.info("UsernameNotFoundException");
			throw new UsernameNotFoundException(siren);
		}

		log.info("UserDetailsServiceImpl fin");
		return UserDetailsImpl.build(user);
	}
	@Transactional
	public UserDetails loadUser(EtablissementEntity user) throws UsernameNotFoundException {
		log.info("UserDetailsServiceImpl début loadUser");
		log.info("user = " + user);
		log.info("UserDetailsServiceImpl fin");
		return UserDetailsImpl.build(user);
	}

}
