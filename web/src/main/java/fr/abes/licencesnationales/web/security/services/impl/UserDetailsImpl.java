package fr.abes.licencesnationales.web.security.services.impl;


import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
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
@Setter
@Getter
public class UserDetailsImpl implements UserDetails {

    private Integer id;
    private String siren;
    private String nameEtab;
    private String password;
    private String email;
    private List<GrantedAuthority> authorities = new ArrayList<>();

    public UserDetailsImpl(EtablissementEntity user) {
        this.id = user.getId();
        this.siren = user.getSiren();
        this.nameEtab = user.getNom();
        this.password = user.getContact().getMotDePasse();
        this.email = user.getContact().getMail();
        this.authorities.add(new SimpleGrantedAuthority(user.getContact().getRole()));
    }

    public String getEmail() {
        return this.email;
    }

    public String getRole() {
        return this.authorities.get(0).getAuthority();
    }

    @Override
    public String getUsername() {
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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

