package com.foodsphere.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for generating and validating JWT tokens.
 * The {@link #generateToken(Authentication)} method generates a JWT token based on the provided authentication object.
 * The {@link #getEmailFromJwt(String)} method extracts the email from the provided JWT token.
 */
@Slf4j
@Service
public class JwtProvider {

    /**
     * This logger_prefix will help us to identify the logs from this class
     */
    private static final String LOGGER_PREFIX = "[JwtProvider] ";

    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    /**
     * Generates a JWT token based on the provided authentication object.
     */
    public String generateToken(Authentication authentication) {
        log.debug(LOGGER_PREFIX + "Generating JWT token for user {}", authentication.getName());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = getRoles(authorities);

        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000))
                .claim("email", authentication.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();

        log.debug(LOGGER_PREFIX + "Generated JWT token: {}", jwt);
        return jwt;
    }

    /**
     * Extracts the email from the provided JWT token.
     *
     * @param jwt the JWT token
     * @return the email
     */
    public String getEmailFromJwt(String jwt) {
        log.debug(LOGGER_PREFIX + "Extracting email from JWT token");

        jwt = jwt.substring(7);

        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        String email = String.valueOf(claims.get("email"));

        log.debug(LOGGER_PREFIX + "Extracted email: {}", email);
        return email;
    }

    private String getRoles(Collection<? extends GrantedAuthority> authorities) {
        Set<String> grantedAuthorities = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return String.join(",", grantedAuthorities);
    }
}