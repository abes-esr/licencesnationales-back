package fr.abes.lnevent.security.services.impl;


import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	EtablissementRepository etablissementRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String siren) throws UsernameNotFoundException {
		ContactEntity user = contactRepository.findBySiren(siren);
		if(user == null) {
			throw new UsernameNotFoundException(siren);
		}
		return fr.abes.lnevent.security.services.impl.UserDetailsImpl.build(user);
	}
	/*public UserDetails getNomEtab(String siren) throws UsernameNotFoundException {
		EtablissementEntity user = etablissementRepository.findBySiren(siren);
		if(user == null) {
			throw new UsernameNotFoundException(siren);
		}
		return fr.abes.lnevent.security.services.impl.UserDetailsImpl.build(user);
	}*/

}
