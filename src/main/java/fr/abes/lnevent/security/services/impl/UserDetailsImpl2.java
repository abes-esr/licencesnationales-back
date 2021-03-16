/*
package fr.abes.lnevent.security.services.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Setter
public class UserDetailsImpl2 implements UserDetails {

	@Autowired
	static EtablissementRepository etablissementRepository;

	private static final long serialVersionUID = 1L;

	private Long id;

	private String siren;


	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	private String isAdmin;



	public UserDetailsImpl(Long id, String siren, String password, Collection<? extends GrantedAuthority> authorities,
						   String isAdmin) {
		this.id = id;
		this.siren = siren;
		this.password = password;
		this.authorities = authorities;
		this.isAdmin = isAdmin;
	}

	public  UserDetailsImpl build(ContactEntity user, String siren) {

		log.info("UserDetailsImpl build début");
		log.info("siren = " + siren);
		setSiren(siren);
		setPassword(user.getMotDePasse());
		setId(user.getId());
		String isAdmin = user.getRole().equals("admin")? "true":"false";
		setIsAdmin(isAdmin);
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(user.getRole()));
		log.info("userPwd = " + user.getMotDePasse());
		return new UserDetailsImpl(user.getId(), siren, user.getMotDePasse(), authorities, isAdmin);
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getPassword() {
		return password;
	}


	public String getIsAdmin() {
		return isAdmin;
	}


	//methode Spring de UserDetails org.springframework.security.core.userdetails
	//on ne peut pas changer le nom mais dans notre cas c'est comme un getSiren
	@Override
	public String getUsername() {
		log.info("getUsername = " + this.siren);
		return this.siren;
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

}
*/
