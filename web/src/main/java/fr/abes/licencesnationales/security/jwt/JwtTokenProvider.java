package fr.abes.licencesnationales.security.jwt;


import fr.abes.licencesnationales.constant.Constant;
import fr.abes.licencesnationales.security.services.impl.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Component
@Slf4j
@ConfigurationProperties(prefix = "jwt.token")
@Getter
@Setter
public class JwtTokenProvider {

    @Autowired
    private Environment env;

    private String secret;
    private int expirationInMs;


    public String generateToken(UserDetailsImpl u) {

        log.debug("expirationInMs = " + expirationInMs);
        log.debug("secret = " + secret);

        log.info("JwtTokenProvider");
        log.info("Début generateToken");
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        log.info("u.getUsername() = " + u.getUsername());//le siren
        log.info("u.getId() = " + u.getId());
        log.info("u.getAuthorities() = " + u.getAuthorities());

        return Jwts.builder()
                .setSubject(u.getUsername()) //siren
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("id", u.getId())
                .claim("siren", u.getUsername()) //le siren
                .claim("nameEtab",u.getNameEtab())//nom de l'étab
                .claim("isAdmin", u.getIsAdmin())
                .claim("isAdminViaAuthorite", u.getAuthorities().iterator().next().toString().equals("admin")? "true":"false")
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
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
                .setSigningKey(secret)
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
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }
}
