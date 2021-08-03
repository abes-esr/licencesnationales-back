package fr.abes.licencesnationales;


import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.List;

public class MockUserDetailsImpl {


    public MockUserDetailsImpl() {

    }
    /**
     *
     */
    public UserDetailsImpl getMockUserDetailsImpl() {
        UserDetailsImpl user = new UserDetailsImpl();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("etab"));

        user.setId(1L);
        user.setNameEtab("test");
        user.setPassword("pass");
        user.setEmail("test@test.fr");
        user.setSiren("123456789");
        user.setAuthorities(authorities);
        user.setAdmin(false);
        return user;
    }

}
