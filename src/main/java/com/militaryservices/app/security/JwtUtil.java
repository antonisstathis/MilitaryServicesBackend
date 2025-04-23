package com.militaryservices.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 5; // 5 hours
    //private static final long EXPIRATION_TIME = 1000 * 4; // 4 seconds

    public JwtUtil() {
        RSAKeyGenerator.produceKeys();
        privateKey = RSAKeyGenerator.loadPrivateKey();
        publicKey = RSAKeyGenerator.loadPublicKey();
    }

    public boolean validateRequest(HttpServletRequest request) {

        return isTokenValid(extractToken(request));
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return "";
        return authHeader.substring(7);
    }

    public String extractUsername(HttpServletRequest request) {
        String token = extractToken(request);
        return extractUsername(token);
    }
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(privateKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        return !isTokenTamperedOrExpired(token);
    }

    public boolean isTokenTamperedOrExpired(String token) {
        try {
            // Use the Jwts.parser() method to verify the JWT signature
            Jwts.parser()
                    .setSigningKey(publicKey)  // Set the public key for signature verification
                    .parseClaimsJws(token);  // This will throw an exception if the signature is invalid

            return false;  // If no exception was thrown, the token is not tampered or expired
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}

