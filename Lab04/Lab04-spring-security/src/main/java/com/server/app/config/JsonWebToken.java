package com.server.app.config;

import com.server.app.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JsonWebToken {

    @Value("${security.jwt.expiration-time}")
    private long tokenTime;

    @Value("${security.jwt.secret-key}")
    private String tokenSecret;

    private SecretKey getTokenKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
    }

    public String createToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(Map.of("id", user.getId()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + tokenTime))
                .signWith(getTokenKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getTokenKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Integer extractIdUser(String token) {
        return extractClaims(token).get("id", Integer.class);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
