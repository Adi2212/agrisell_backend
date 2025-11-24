package com.agrisell.security;

import java.security.Key;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	private final long EXPIRATION_TIME = 1000 * 60 * 60 * 5; // 5 hours

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	// ✅ Updated: added userId in token claims
	public String generateToken(Long userId, String email, String role) {
		return Jwts.builder().setSubject(email).claim("userId", userId) // Add userId claim
				.claim("role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

	 public String extractUsername(String token) {
	        return extractAllClaims(token).getSubject();
	    }

	    public Long extractUserId(String token) {
            return extractAllClaims(token).get("userId", Long.class);
	    }

	    public String extractRole(String token) {
	        return extractAllClaims(token).get("role", String.class);
	    }

	    public boolean isTokenValid(String token) {
	        try {
	            return !extractAllClaims(token).getExpiration().before(new Date());
	        } catch (Exception e) {
	            return false;
	        }
	    }

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	
}
