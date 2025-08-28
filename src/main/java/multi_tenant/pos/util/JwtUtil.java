package multi_tenant.pos.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import multi_tenant.pos.dto.UserTokenDTO;

@Component
public class JwtUtil {

    // 🔐 Clave leída desde application.properties
    @Value("${jwt.secret}")
    private String secret;

    // 🔑 Método que devuelve la clave a partir del base64
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ método público para extraer los claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ Método para generar token
    public String generateToken(UserTokenDTO userTokenDTO) {
        return Jwts.builder()
                .subject(userTokenDTO.getUsername())
                .claim("id", userTokenDTO.getId())
                .claim("rol", userTokenDTO.getRol())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10 * 1000)) // 10 horas * 100 para que sean mas de 40 dias
                .signWith(getSigningKey())
                .compact();
    }

    // ✅ Método para extraer username
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            return null; // o loggear el error si querés
        }
    }

    // ✅ Validación de token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ✅ Verificación de expiración
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
