package utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtUtil {

    // 256-bit secure key for HS256
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    // Validate token
    public boolean validateToken(String token) {
        try {
            if (TokenBlacklist.isBlacklisted(token)) {
                return false;  // token was logged out
            }
            getAllClaims(token); // throws if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extract username
    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    // Extract role
    public String getRoleFromToken(String token) {
        return getAllClaims(token).get("role", String.class);
    }

    // Central parsing method
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Allow AuthService to access the key
    public static SecretKey getSecretKey() {
        return SECRET_KEY;
    }
}