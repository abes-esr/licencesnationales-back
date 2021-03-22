package fr.abes.lnevent.security.services.impl;

import fr.abes.lnevent.entities.EtablissementEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Setter @Getter
public class UserDetailsImpl implements UserDetails {


	private Long id;
	private String siren;
	private String nameEtab;//nom etab
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private String isAdmin;



	public UserDetailsImpl(Long id, String siren, String nameEtab, String password, Collection<? extends GrantedAuthority> authorities,
						   String isAdmin) {
		this.id = id;
		this.siren = siren;
		this.nameEtab=nameEtab;
		this.password = password;
		this.authorities = authorities;
		this.isAdmin = isAdmin;
	}

	public static UserDetailsImpl build(EtablissementEntity user) {

		log.info("UserDetailsImpl build début");
		String isAdmin = user.getContact().role.equals("admin")? "true":"false";
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(user.getContact().role));
		log.info("userPwd = " + user.getContact().motDePasse);
		return new UserDetailsImpl(user.getId(), user.getSiren(), user.getName(), user.getContact().motDePasse, authorities, isAdmin);
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}


	//methode Spring de UserDetails org.springframework.security.core.userdetails
	//on ne peut pas changer le nom mais dans notre cas c'est comme un getSiren
	@Override
	public String getUsername() {
		log.info("getUsername = " + siren);
		return siren;
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

