package org.acme.plataforma.courses.application.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.acme.plataforma.courses.application.dto.LoginRequest;
import org.acme.plataforma.courses.application.dto.RegisterRequest;
import org.acme.plataforma.courses.application.dto.TokenPair;
import org.acme.plataforma.courses.application.service.impl.AuthServiceImpl;
import org.acme.plataforma.courses.domain.entity.User;
import org.acme.plataforma.courses.infra.exception.BadRequestException;
import org.acme.plataforma.courses.infra.exception.UnauthorizedException;
import org.acme.plataforma.courses.infra.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    TokenService tokenService;

    @InjectMocks
    AuthServiceImpl authService;

    private MockedStatic<BcryptUtil> bcryptUtilMock;

    @BeforeEach
    void setUp() {
        bcryptUtilMock = mockStatic(BcryptUtil.class);
    }

    @AfterEach
    void tearDown() {
        bcryptUtilMock.close();
    }

    @Test
    void login_ShouldReturnTokenPair_WhenCredentialsAreValid() {
        String email = "user@example.com";
        String password = "validPassword";
        LoginRequest request = new LoginRequest(email, password);

        User user = new User();
        user.email = email;
        user.passwordHash = "hashedPassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(BcryptUtil.matches(password, user.passwordHash)).thenReturn(true);
        when(tokenService.generateAccessToken(user)).thenReturn("access");
        when(tokenService.generateRefreshToken(user)).thenReturn("refresh");

        TokenPair result = authService.login(request);

        assertNotNull(result);
        assertEquals("access", result.accessToken());
        assertEquals("refresh", result.refreshToken());

        verify(userRepository).findByEmail(email);
        verify(tokenService).generateAccessToken(user);
        verify(tokenService).generateRefreshToken(user);
    }

    @Test
    void login_ShouldThrowUnauthorizedException_WhenEmailNotFound() {
        String email = "notfound@example.com";
        LoginRequest request = new LoginRequest(email, "anyPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authService.login(request));

        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }

    @Test
    void login_ShouldThrowUnauthorizedException_WhenPasswordDoesNotMatch() {
        String email = "user@example.com";
        String wrongPassword = "wrongPassword";
        LoginRequest request = new LoginRequest(email, wrongPassword);

        User user = new User();
        user.email = email;
        user.passwordHash = "correctHash";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(BcryptUtil.matches(wrongPassword, user.passwordHash)).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authService.login(request));

        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }

    // ------------------- REGISTER TESTS -------------------
    // ATENÇÃO: Assumindo que o construtor de RegisterRequest é (password, email, name)
    // Se for diferente, ajuste as chamadas abaixo.

    @Test
    void register_ShouldReturnTokenPair_WhenRegistrationIsValid() {
        String email = "newuser@example.com";
        String password = "Valid123";
        String name = "New User";
        // Ordem: password, email, name
        RegisterRequest request = new RegisterRequest(password, email, name);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(BcryptUtil.bcryptHash(password)).thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.email = email;
        savedUser.name = name;
        savedUser.passwordHash = "hashedPassword";

        doNothing().when(userRepository).persist(any(User.class));

        when(tokenService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        TokenPair result = authService.register(request);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());

        verify(userRepository).existsByEmail(email);
        verify(userRepository).persist(any(User.class));
        verify(tokenService).generateAccessToken(any(User.class));
        verify(tokenService).generateRefreshToken(any(User.class));
    }

    @Test
    void register_ShouldThrowBadRequestException_WhenEmailAlreadyExists() {
        String email = "existing@example.com";
        String password = "Valid123";
        String name = "Name";
        RegisterRequest request = new RegisterRequest(password, email, name);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(request));

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).persist(any(User.class));
        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }

    @Test
    void register_ShouldThrowBadRequestException_WhenPasswordIsTooShort() {
        String email = "user@example.com";
        String shortPassword = "short";        // menos de 8 caracteres
        String name = "Name";
        // Ordem: password, email, name
        RegisterRequest request = new RegisterRequest(shortPassword, email, name);

        // Como o código chama existsByEmail antes de validar a senha,
        // precisamos garantir que o e-mail não existe (retorna false)
        when(userRepository.existsByEmail(email)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(request));

        assertEquals("Senha deve ter no mínimo 8 caracteres", exception.getMessage());

        // existsByEmail foi chamado e retornou false
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).persist(any(User.class));
        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }

    @Test
    void register_ShouldThrowBadRequestException_WhenPasswordHasNoLetters() {
        String email = "user@example.com";
        String passwordNoLetters = "12345678";   // 8 caracteres, só números
        String name = "Name";
        RegisterRequest request = new RegisterRequest(passwordNoLetters, email, name);

        when(userRepository.existsByEmail(email)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(request));

        assertEquals("Senha deve conter letras", exception.getMessage());

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).persist(any(User.class));
        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }

    @Test
    void register_ShouldThrowBadRequestException_WhenPasswordHasNoDigits() {
        String email = "user@example.com";
        String passwordNoDigits = "abcdefgh";    // 8 caracteres, só letras
        String name = "Name";
        RegisterRequest request = new RegisterRequest(passwordNoDigits, email, name);

        when(userRepository.existsByEmail(email)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(request));

        assertEquals("Senha deve conter números", exception.getMessage());

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).persist(any(User.class));
        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }
}
