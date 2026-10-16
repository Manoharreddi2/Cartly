package com.cartly.order.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtUtils {

    private final SecretKey secretKey;

    public JwtUtils(@Value("${app.jwt.secret:CartlySuperSecretKeyForJWTSigningAndVerificationMustBeAtLeast256BitsLong2026!}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Long getUserIdFromHeader(String header) {
        if (header == null) {
            return 2L;
        }
        Claims claims = getClaimsFromHeader(header);
        return claims != null ? claims.get("userId", Long.class) : 2L;
    }

    public String getEmailFromHeader(String header) {
        if (header == null) {
            return "customer@cartly.com";
        }
        Claims claims = getClaimsFromHeader(header);
        return claims != null ? claims.getSubject() : "customer@cartly.com";
    }

    @SuppressWarnings("unchecked")
    public boolean isAdmin(String header) {
        if (header == null) return false;
        Claims claims = getClaimsFromHeader(header);
        if (claims == null) return false;
        List<String> roles = claims.get("roles", List.class);
        return roles != null && roles.contains("ROLE_ADMIN");
    }

    private Claims getClaimsFromHeader(String header) {
        try {
            String token = header.startsWith("Bearer ") ? header.substring(7) : header;
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
