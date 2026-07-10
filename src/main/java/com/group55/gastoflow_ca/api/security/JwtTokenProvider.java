package com.group55.gastoflow_ca.api.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.InvalidTokenException;
import com.group55.gastoflow_ca.core.interfaces.auth.ITokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider implements ITokenProvider {

    private static final String CLAIM_NOME = "nome";
    private static final String CLAIM_EMAIL = "emailAddress";
    private static final String CLAIM_LOGIN = "login";
    private static final String CLAIM_USER_TYPE_ID = "userTypeId";
    private static final String CLAIM_USER_TYPE_NAME = "userTypeName";
    private static final String CLAIM_PERMISSIONS = "permissions";

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(UserToken userToken) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationMs);

        UserType userType = userToken.getUserType();

        List<String> permissionNames = userType.getPermissions().stream()
                .map(Permission::name)
                .toList();

        return Jwts.builder()
                .subject(userToken.getUserId().toString())
                .claim(CLAIM_NOME, userToken.getNome())
                .claim(CLAIM_EMAIL, userToken.getEmailAddress())
                .claim(CLAIM_LOGIN, userToken.getLogin())
                .claim(CLAIM_USER_TYPE_ID, userType.getId().toString())
                .claim(CLAIM_USER_TYPE_NAME, userType.getName())
                .claim(CLAIM_PERMISSIONS, permissionNames)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(key)
                .compact();
    }

    @Override
    public UserToken parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());
            String nome = claims.get(CLAIM_NOME, String.class);
            String emailAddress = claims.get(CLAIM_EMAIL, String.class);
            String login = claims.get(CLAIM_LOGIN, String.class);
            UUID userTypeId = UUID.fromString(claims.get(CLAIM_USER_TYPE_ID, String.class));
            String userTypeName = claims.get(CLAIM_USER_TYPE_NAME, String.class);

            @SuppressWarnings("unchecked")
            List<String> permissionNames = claims.get(CLAIM_PERMISSIONS, List.class);

            Set<Permission> permissions = permissionNames.stream()
                    .map(Permission::valueOf)
                    .collect(Collectors.toSet());

            UserType userType = UserType.create(userTypeId, userTypeName, permissions);

            return new UserToken(userId, nome, emailAddress, login, userType);

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }
}
