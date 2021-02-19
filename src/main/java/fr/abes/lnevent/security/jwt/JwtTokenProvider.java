package fr.abes.lnevent.security.jwt;

import fr.abes.lnevent.constant.Constant;
import fr.abes.lnevent.dto.User;
import fr.abes.lnevent.entities.ContactRow;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(ContactRow u) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(u.getSiren())// USER_KEY de la base
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("userId", u.getId())
                .claim("userRole", u.getRole())
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

    /*public ContactRow getUtilisateurFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        ContactRow u = new ContactRow();
        u.setId(claims.get("userId").toString());
        u.setSiren(claims.get("userSiren").toString());
        u.setIsAdmin(Boolean.parseBoolean((String) claims.get("userRole")));

        return u;
    }*/

    public String getSirenFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
