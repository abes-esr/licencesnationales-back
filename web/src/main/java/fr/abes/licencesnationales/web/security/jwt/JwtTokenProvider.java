package fr.abes.licencesnationales.web.security.jwt;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.web.exception.InvalidTokenException;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class JwtTokenProvider {

    @Autowired
    private Environment env;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expirationInMs}")
    private int expirationInMs;

    public String generateToken(UserDetailsImpl u) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);


        return Jwts.builder()
                .setSubject(u.getUsername()) //siren
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("id", u.getId())
                .claim("siren", u.getUsername()) //le siren
                .claim("nameEtab", u.getNameEtab())//nom de l'étab
                .claim("role", u.getRole())
                .claim("isAdminViaAuthorite", u.getAuthorities().iterator().next().toString().equals("admin") ? "true" : "false")
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

    public String getSirenFromJwtToken(String token) throws InvalidTokenException {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (IllegalArgumentException ex) {
            throw new InvalidTokenException(Constant.ERROR_TOKEN_INVALID);
        }
    }
}
