package com.eventmanagement.service;

import com.eventmanagement.dto.LoginRequest;
import com.eventmanagement.dto.LoginResponse;
import com.eventmanagement.dto.SignupRequest;
import com.eventmanagement.entity.User;
import com.eventmanagement.entity.UserRole;
import com.eventmanagement.exception.BadRequestException;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.config.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignupSuccess() {
        SignupRequest request = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(User.builder()
                .id(1L)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build());

        var response = authService.signup(request);

        assertNotNull(response);
        assertEquals(request.getEmail(), response.getEmail());
        verify(emailService, times(1)).sendWelcomeEmail(request.getEmail(), request.getFirstName());
    }

    @Test
    void testSignupEmailAlreadyExists() {
        SignupRequest request = SignupRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.signup(request));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .id(1L)
                .email(request.getEmail())
                .password("hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.generateToken(user.getEmail(), user.getId(), user.getRole().toString()))
                .thenReturn("jwtToken");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        User user = User.builder()
                .id(1L)
                .email(request.getEmail())
                .password("hashedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }
}
