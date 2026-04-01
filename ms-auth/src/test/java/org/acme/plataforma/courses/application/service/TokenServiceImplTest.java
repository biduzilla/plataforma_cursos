package org.acme.plataforma.courses.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.acme.plataforma.courses.application.dto.TokenPair;
import org.acme.plataforma.courses.application.service.impl.TokenServiceImpl;
import org.acme.plataforma.courses.domain.entity.RefreshToken;
import org.acme.plataforma.courses.domain.entity.User;
import org.acme.plataforma.courses.domain.enums.Role;
import org.acme.plataforma.courses.infra.exception.UnauthorizedException;
import org.acme.plataforma.courses.infra.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {
    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    TokenServiceImpl tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.id = UUID.randomUUID();
        user.email = "test@example.com";
        user.name = "Test User";
        user.role = Role.STUDENT;
    }

    @Test
    void testGenerateAcessToken() {
        try (MockedStatic<Jwt> jwtMock = Mockito.mockStatic(Jwt.class)) {
            JwtClaimsBuilder builderMock = mock(JwtClaimsBuilder.class);
            jwtMock.when(() -> Jwt.issuer(anyString())).thenReturn(builderMock);
            when(builderMock.subject(anyString())).thenReturn(builderMock);
            when(builderMock.groups(anyString())).thenReturn(builderMock);
            when(builderMock.claim(anyString(), any())).thenReturn(builderMock);
            when(builderMock.expiresIn(any(Duration.class))).thenReturn(builderMock);
            when(builderMock.sign()).thenReturn("dummy.jwt.token");

            String token = tokenService.generateAccessToken(user);

            assertEquals("dummy.jwt.token", token);
            jwtMock.verify(() -> Jwt.issuer("ms-auth"));
            verify(builderMock).subject(user.id.toString());
            verify(builderMock).groups(user.role.name());
            verify(builderMock).claim("email", user.email);
            verify(builderMock).claim("name", user.name);
            verify(builderMock).expiresIn(Duration.ofMinutes(15));
            verify(builderMock).sign();
        }
    }

    @Test
    void testGenerateRefreshToken() {
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        String rawToken = tokenService.generateRefreshToken(user);

        verify(refreshTokenRepository).persist(captor.capture());
        RefreshToken persisted = captor.getValue();
        assertNotNull(persisted);
        assertNotNull(persisted.tokenHash);
        assertNotNull(persisted.expiresAt);
        assertEquals(user, persisted.user);
        assertFalse(persisted.used);
        assertTrue(persisted.expiresAt.isAfter(LocalDateTime.now().minusSeconds(1)));
        assertTrue(persisted.expiresAt.isBefore(LocalDateTime.now().plusDays(7).plusSeconds(1)));
        assertNotNull(rawToken);
        assertFalse(rawToken.isBlank());

        String expectedHash = invokeHashToken(rawToken);
        assertEquals(expectedHash, persisted.tokenHash);
    }

    @Test
    void testRotateRefreshToken_tokenNotFound() {
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        String rawToken = "invalid.token";
        assertThrows(UnauthorizedException.class, () -> tokenService.rotateRefreshToken(rawToken));
        verify(refreshTokenRepository, never()).revokeAllFromUser(any());
    }

    @Test
    void testRotateRefreshToken_success() {
        RefreshToken storedToken = new RefreshToken();
        storedToken.user = user;
        storedToken.tokenHash = "someHash";
        storedToken.expiresAt = LocalDateTime.now().plusDays(7);
        storedToken.used = false;

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(storedToken));

        try (MockedStatic<Jwt> jwtMock = Mockito.mockStatic(Jwt.class)) {
            JwtClaimsBuilder builderMock = mock(JwtClaimsBuilder.class);
            jwtMock.when(() -> Jwt.issuer(anyString())).thenReturn(builderMock);
            when(builderMock.subject(anyString())).thenReturn(builderMock);
            when(builderMock.groups(anyString())).thenReturn(builderMock);
            when(builderMock.claim(anyString(), any())).thenReturn(builderMock);
            when(builderMock.expiresIn(any(Duration.class))).thenReturn(builderMock);
            when(builderMock.sign()).thenReturn("newAccessToken", "newRefreshToken"); // apenas o access token será usado

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            String rawToken = "raw.refresh.token";

            TokenPair tokenPair = tokenService.rotateRefreshToken(rawToken);

            assertTrue(storedToken.used);

            verify(refreshTokenRepository).persist(captor.capture());
            RefreshToken newRefresh = captor.getValue();
            assertNotNull(newRefresh);
            assertEquals(user, newRefresh.user);
            assertNotNull(newRefresh.tokenHash);
            assertNotNull(newRefresh.expiresAt);
            assertFalse(newRefresh.used);

            assertEquals("newAccessToken", tokenPair.accessToken());
            assertNotNull(tokenPair.refreshToken());
            assertFalse(tokenPair.refreshToken().isBlank());
            assertTrue(tokenPair.refreshToken().matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
        }
    }

    @Test
    void testRotateRefreshToken_tokenInvalid() {
        RefreshToken storedToken = new RefreshToken();
        storedToken.user = user;
        storedToken.tokenHash = "someHash";
        storedToken.expiresAt = LocalDateTime.now().minusDays(1);
        storedToken.used = false;

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(storedToken));

        String rawToken = "expired.token";
        assertThrows(UnauthorizedException.class, () -> tokenService.rotateRefreshToken(rawToken));

        verify(refreshTokenRepository).revokeAllFromUser(user.id);
        assertFalse(storedToken.used);
    }

    @Test
    void testRevokeRefreshToken_tokenExists() {
        RefreshToken storedToken = new RefreshToken();
        storedToken.used = false;

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(storedToken));

        String rawToken = "token.to.revoke";
        tokenService.revokeRefreshToken(rawToken);

        assertTrue(storedToken.used);
        verify(refreshTokenRepository, never()).persist((RefreshToken) any());
    }

    @Test
    void testRevokeRefreshToken_tokenNotExists() {
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        String rawToken = "nonexistent.token";
        tokenService.revokeRefreshToken(rawToken);

        verify(refreshTokenRepository, never()).persist((RefreshToken) any());
    }

    private String invokeHashToken(String rawToken) {
        try {
            java.lang.reflect.Method method = TokenServiceImpl.class.getDeclaredMethod("hashToken", String.class);
            method.setAccessible(true);
            return (String) method.invoke(tokenService, rawToken);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao invocar método hashToken", e);
        }
    }
}
