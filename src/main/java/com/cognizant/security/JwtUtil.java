package com.cognizant.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Handles JWT creation and validation.
 * Add to pom.xml:
 *   <dependency>
 *     <groupId>io.jsonwebtoken</groupId>
 *     <artifactId>jjwt-api</artifactId>
 *     <version>0.11.5</version>
 *   </dependency>
 *   <dependency>
 *     <groupId>io.jsonwebtoken</groupId>
 *     <artifactId>jjwt-impl</artifactId>
 *     <version>0.11.5</version>
 *     <scope>runtime</scope>
 *   </dependency>
 *   <dependency>
 *     <groupId>io.jsonwebtoken</groupId>
 *     <artifactId>jjwt-jackson</artifactId>
 *     <version>0.11.5</version>
 *     <scope>runtime</scope>
 *   </dependency>
 */
@Component
public class JwtUtil {

    // Set this in application.properties: jwt.secret=your-256-bit-secret-key-here-make-it-long
    @Value("${jwt.secret}")
    private String secret;

    // Token valid for 24 hours
    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /** Generate a token for a user */
    public String generateToken(String userName, String role) {
        return Jwts.builder()
                .setSubject(userName)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extract username from token */
    public String extractUserName(String token) {
        return getClaims(token).getSubject();
    }

    /** Extract role from token */
    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    /** Validate token — returns true if valid and not expired */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
