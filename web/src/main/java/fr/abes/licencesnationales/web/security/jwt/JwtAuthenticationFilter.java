package fr.abes.licencesnationales.web.security.jwt;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.web.security.cache.LoginAttemptService;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
            final String ip = getClientIP();
            if (loginAttemptService.isBlocked(ip)) {
                throw new RuntimeException(Constant.IP_BLOCKED);
            }

            String jwt = tokenProvider.getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String siren = tokenProvider.getSirenFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(siren);

                //on vérifie que pour le siren et le pass trouvé c'est bien celui du jeton?
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            request.setAttribute("expired", ex.getMessage());
        }
        catch (Exception ex) {
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

