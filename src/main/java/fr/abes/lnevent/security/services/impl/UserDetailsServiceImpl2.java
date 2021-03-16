/*
package fr.abes.lnevent.security.services.impl;


import fr.abes.lnevent.entities.ContactEntity;

import fr.abes.lnevent.repository.EtablissementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDetailsServiceImpl2 implements UserDetailsService {

	@Autowired
	EtablissementRepository etablissementRepository;

	UserDetailsImpl userDetailsImpl;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String siren) throws UsernameNotFoundException {
		log.info("UserDetailsServiceImpl début");
		log.info("siren = " + siren);
		ContactEntity user = etablissementRepository.getContactBySiren(siren);
		log.info("user = " + user);
		if(user == null) {
			log.info("UsernameNotFoundException");
			throw new UsernameNotFoundException(siren);
		}
		userDetailsImpl = new UserDetailsImpl(null,null,null,null,null);
		userDetailsImpl.build(user, siren);
		log.info("UserDetailsServiceImpl fin");
		return userDetailsImpl;
	}
}
*/
