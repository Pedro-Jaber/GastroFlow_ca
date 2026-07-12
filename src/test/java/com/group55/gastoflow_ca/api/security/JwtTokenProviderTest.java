package com.group55.gastoflow_ca.api.security;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.InvalidTokenException;

class JwtTokenProviderTest {

    private static final String SECRET = "01234567890123456789012345678901234567890123456789";
    private static final long ONE_HOUR_MS = 3_600_000;

    private JwtTokenProvider providerWithExpiration(long expirationMs) {
        return new JwtTokenProvider(SECRET, expirationMs);
    }

    private UserToken sampleUserToken() {
        UserType userType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.CREATE_USER, Permission.READ_ALL_USER));
        return new UserToken(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", userType);
    }

    @Test
    void shouldGenerateNonEmptyToken() {
        JwtTokenProvider provider = providerWithExpiration(ONE_HOUR_MS);
        UserToken userToken = sampleUserToken();

        String token = provider.generateToken(userToken);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldRoundTripAllUserTokenFieldsThroughGenerateAndParse() {
        JwtTokenProvider provider = providerWithExpiration(ONE_HOUR_MS);
        UserToken userToken = sampleUserToken();

        String token = provider.generateToken(userToken);
        UserToken parsed = provider.parseToken(token);

        assertThat(parsed.getUserId()).isEqualTo(userToken.getUserId());
        assertThat(parsed.getNome()).isEqualTo(userToken.getNome());
        assertThat(parsed.getEmailAddress()).isEqualTo(userToken.getEmailAddress());
        assertThat(parsed.getLogin()).isEqualTo(userToken.getLogin());
        assertThat(parsed.getUserType().getId()).isEqualTo(userToken.getUserType().getId());
        assertThat(parsed.getUserType().getName()).isEqualTo(userToken.getUserType().getName());
        assertThat(parsed.getUserType().getPermissions())
                .isEqualTo(userToken.getUserType().getPermissions());
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenIsMalformed() {
        JwtTokenProvider provider = providerWithExpiration(ONE_HOUR_MS);

        assertThatThrownBy(() -> provider.parseToken("this-is-not-a-jwt"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenIsBlank() {
        JwtTokenProvider provider = providerWithExpiration(ONE_HOUR_MS);

        assertThatThrownBy(() -> provider.parseToken(""))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenIsExpired() {
        JwtTokenProvider provider = providerWithExpiration(-ONE_HOUR_MS);
        UserToken userToken = sampleUserToken();

        String expiredToken = provider.generateToken(userToken);

        assertThatThrownBy(() -> provider.parseToken(expiredToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenWasSignedWithDifferentSecret() {
        JwtTokenProvider providerA = providerWithExpiration(ONE_HOUR_MS);
        JwtTokenProvider providerB = new JwtTokenProvider(
                "98765432109876543210987654321098765432109876543210", ONE_HOUR_MS);
        UserToken userToken = sampleUserToken();

        String tokenSignedByA = providerA.generateToken(userToken);

        assertThatThrownBy(() -> providerB.parseToken(tokenSignedByA))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenIsTampered() {
        JwtTokenProvider provider = providerWithExpiration(ONE_HOUR_MS);
        UserToken userToken = sampleUserToken();

        String token = provider.generateToken(userToken);
        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');

        assertThatThrownBy(() -> provider.parseToken(tamperedToken))
                .isInstanceOf(InvalidTokenException.class);
    }
}
