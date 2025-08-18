package com.alibou.book.authManagement.service;

import com.alibou.book.auth.AuthenticationRequest;
import com.alibou.book.auth.AuthenticationResponse;
import com.alibou.book.auth.RegistrationRequest;
import com.alibou.book.auth.service.AuthenticationService;
import com.alibou.book.email.EmailService.EmailService;
import com.alibou.book.email.EmailService.EmailTemplateName;
import com.alibou.book.roleManagement.dao.RoleDAOJPA;
import com.alibou.book.roleManagement.entity.Role;
import com.alibou.book.security.JwtService;
import com.alibou.book.tokenManagement.dao.TokenRepository;
import com.alibou.book.tokenManagement.entity.Token;
import com.alibou.book.userManagement.dao.UserDAOJPA;
import com.alibou.book.userManagement.entity.User;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserDAOJPA userRepository;
    @Mock
    private RoleDAOJPA roleDAOJPA;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private AuthenticationService authenticationService;
    @Test
    void testRegister() throws MessagingException {
        RegistrationRequest request = RegistrationRequest.builder()
                .first_name("John")
                .last_name("Doe")
                .email("john.doe@example.com")
                .password("Password123!")
                .build();

        Role roleClient = new Role();
        roleClient.setName("ROLE_CLIENT");

        User user = User.builder()
                .first_name(request.getFirst_name())
                .last_name(request.getLast_name())
                .email(request.getEmail())
                .password("encodedPass")
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(roleClient))
                .build();

        when(roleDAOJPA.findByName("ROLE_CLIENT")).thenReturn(Optional.of(roleClient));
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenRepository.save(any())).thenReturn(null);

        authenticationService.register(request);

        verify(roleDAOJPA).findByName("ROLE_CLIENT");
        verify(passwordEncoder).encode("Password123!");
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any());
    }

    @Test
    void testAuthenticate_ShouldReturnToken_WhenCredentialsValid() {
        // given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("Password123!")
                .build();

        User mockUser = User.builder()
                .first_name("John")
                .last_name("Doe")
                .email(request.getEmail())
                .password("encodedPass")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        String expectedToken = "jwt-token-123";
        when(jwtService.generateToken(anyMap(), eq(mockUser))).thenReturn(expectedToken);

        // when
        AuthenticationResponse response = authenticationService.authenticate(request);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(anyMap(), eq(mockUser));
    }

    @Test
    void shouldActivateAccount_WhenTokenIsValid() throws MessagingException {
        // given
        User user = User.builder()
                .id(1)
                .email("john@example.com")
                .enabled(false)
                .build();

        Token token = Token.builder()
                .token("valid-token")
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // when
        authenticationService.activateAccount("valid-token");

        // then
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
        assert user.isEnabled();
        assert token.getValidatedAt() != null;
    }

    @Test
    void shouldThrowException_WhenTokenNotFound() {
        // given
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> authenticationService.activateAccount("invalid-token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid token");

        verify(tokenRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldResendValidationEmail_WhenTokenExpired() throws MessagingException {
        // given
        User user = User.builder()
                .id(1)
                .email("john@example.com")
                .enabled(false)
                .build();

        Token expiredToken = Token.builder()
                .token("expired-token")
                .user(user)
                .expiresAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        // when + then
        assertThatThrownBy(() -> authenticationService.activateAccount("expired-token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Activation token has expired");
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        // given
        User user = User.builder().id(1).enabled(false).build();
        Token token = Token.builder()
                .token("valid-token")
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> authenticationService.activateAccount("valid-token"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldGenerateAndSaveActivationToken() {
        // given
        User user = User.builder()
                .id(1)
                .email("john@example.com")
                .build();

        // when
        String token = authenticationService.generateAndSaveActivationToken(user);

        // then
        assertEquals(token.length(),6);

        // verify token saved
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        Token savedToken = tokenCaptor.getValue();
        assertEquals(savedToken.getUser(),user);
        assertEquals(savedToken.getToken(),token);
    }

    @Test
    void shouldGenerateActivationCodeOfCorrectLengthAndDigitsOnly() {
        // given
        int length = 6;

        // when
        String code = authenticationService.generateActivationCode(length);

        // then
        assertEquals(length, code.length(), "Generated code should have the correct length");
        assertTrue(code.matches("\\d+"), "Generated code should contain only digits");
    }
}
