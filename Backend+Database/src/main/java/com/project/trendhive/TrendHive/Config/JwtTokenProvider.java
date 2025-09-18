package com.project.trendhive.TrendHive.Config;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecretKey;
    private final long jwtExpirationInMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-milliseconds}") long expiration) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.jwtExpirationInMs = expiration;
    }

    /**
     * Generates a token and adds the user's full name and role as custom claims.
     */
    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Get the principal object
        Object principal = authentication.getPrincipal();

        String email;
        String fullName = ""; // Default to empty string
        String role = "";     // Default to empty string

        // Safely check the type of the principal
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            email = userPrincipal.getUsername();
            fullName = userPrincipal.getFullName();
            role = userPrincipal.getRole();
        } else {
            // Fallback for other principal types (e.g., a simple string)
            email = authentication.getName();
        }

        return Jwts.builder()
                .setSubject(email)
                .claim("fullName", fullName)
                .claim("roles", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            System.out.println("JWT validation failed: " + ex.getMessage());
        }
        return false;
    }

    public String getUsernameFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}