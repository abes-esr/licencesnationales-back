package fr.abes.licencesnationales.web.security.services.impl;


import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import fr.abes.licencesnationales.web.security.exception.DonneeIncoherenteBddException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Setter @Getter
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

	/*@Autowired
	EtablissementRepository etablissementRepository;*/


	private Long id;
	private String siren;
	private String nameEtab;//nom etab
	private String password;
	private String email;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean isAdmin;



	public UserDetailsImpl(Long id, String siren, String nameEtab, String password, String email,  Collection<? extends GrantedAuthority> authorities,
						   boolean isAdmin) {
		this.id = id;
		this.siren = siren;
		this.nameEtab=nameEtab;
		this.password = password;
		this.email = email;
		this.authorities = authorities;
		this.isAdmin = isAdmin;
	}


	public static UserDetailsImpl build(EtablissementEntity user) throws DonneeIncoherenteBddException, BadCredentialsException {

		log.info("UserDetailsImpl build début");
		String role = user.getContact().getRole();
		boolean isAdmin;
		if(role==null || role.equals("")){
			throw new DonneeIncoherenteBddException("Le role utilisateur est absent de la bdd");
		}
		else {
			isAdmin = role.equals("admin") ? true : false;
		}
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		log.info("userPwd = " + user.getContact().getMotDePasse());
		return new UserDetailsImpl(user.getId(), user.getSiren(), user.getName(), user.getContact().getMotDePasse(), user.getContact().getMail(),authorities, isAdmin);
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}


	//methode Spring de UserDetails org.springframework.fr.abes.licencesnationales.web.security.core.userdetails
	//on ne peut pas changer le nom mais dans notre cas c'est comme un getSiren
	@Override
	public String getUsername() {
		log.info("getUsername = " + siren);
		return this.siren;
	}

	@Override
	public String getPassword() {
		return this.password;
	}


	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	public String getEmail() {
		return email;
	}
}

