package fr.abes.lnevent.security.jwt;

import fr.abes.lnevent.constant.Constant;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.security.services.impl.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Component
@Slf4j
public class JwtTokenProvider {

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private Environment env;


    //@Value("${app.jwtSecret}")
    //private String jwtSecret = env.getProperty("jwtSecret");
    private String jwtSecret = "1234";

    //@Value("${app.jwtExpirationInMs}")
    //private String jwtExpirationInMs = env.getProperty("jwtExpirationInMs");
    private int jwtExpirationInMs = 1234;

    public String generateToken(UserDetailsImpl u) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        String nomEtab = etablissementRepository.getNomEtabBySiren(u.getUsername());


        log.info("u.getUsername() = " + u.getUsername());
        log.info("u.getId() = " + u.getId());
        //log.info("u.getAuthorities() = " + u.getAuthorities());
        log.info("nomEtab = " + nomEtab);

        return Jwts.builder()
                .setSubject(u.getUsername()) //c'est le siren, mais comme provient de spring UserDetailsImpl qui implemente org.springframework.security.core.userdetails, on ne peut pas changer le nom
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("id", u.getId())
                .claim("name", u.getUsername()) //le siren
                .claim("isAdmin", u.getIsAdmin())
                .claim("isAdminViaAuthorite", u.getAuthorities().iterator().next().toString().equals("admin")? "true":"false")
                .claim("nameEtab", nomEtab)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error(Constant.JWT_SIGNATURE_INVALID, ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error(Constant.JWT_TOKEN_INVALID, ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error(Constant.JWT_TOKEN_EXPIRED, ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error(Constant.JWT_TOKEN_UNSUPPORTED, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error(Constant.JWT_CLAIMS_STRING_EMPTY, ex.getMessage());
        }
        return false;
    }
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /*public ContactEntity getUtilisateurFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        ContactEntity u = new ContactEntity();
        u.setId((Long)claims.get("userId"));
        u.setSiren(claims.get("userSiren").toString());
        u.setRole(claims.get("userRole").toString());

        return u;
    }*/

    public String getSirenFromJwtToken(String token) {
        log.info("token = " + token);
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
