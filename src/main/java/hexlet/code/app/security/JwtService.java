
package hexlet.code.app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.JwtException;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(
            @org.springframework.beans.factory.annotation.Value(
                    "${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now))
                .expiration(new Date(now + 86400000))
                .signWith(key)
                .compact();
    }

    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

}
