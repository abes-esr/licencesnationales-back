package fr.abes.lnevent.security.jwt;

import fr.abes.lnevent.constant.Constant;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.security.cache.LoginAttemptService;
import fr.abes.lnevent.security.services.impl.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.debug(Constant.ENTER_DOFILTERINTERNAL);
            log.info("doFilterInternal début");
            final String ip = getClientIP();
            if (loginAttemptService.isBlocked(ip)) {
                throw new RuntimeException(Constant.IP_BLOCKED);
            }

            String jwt = tokenProvider.getJwtFromRequest(request);
            log.info("StringUtils.hasText(jwt) = " + StringUtils.hasText(jwt));
            log.info("Si false, signifie que nous n'avons pas déjà de token, donc on vient se connecter donc on est redirigé vers l'authentification" );

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String siren = tokenProvider.getSirenFromJwtToken(jwt);
                log.info("siren = " + siren);
                log.info("On charge le tuple en bdd qui match ave le siren dans userdetails ");
                UserDetails userDetails = userDetailsService.loadUserByUsername(siren);

                //on vérifie que pour le siren et le pass trouvé c'est bien celui du jeton?
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error(Constant.ERROR_AUTHENTICATION_IN_SECURITY_CONTEXT, ex);
        }

        filterChain.doFilter(request, response);
    }

    private final String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}

