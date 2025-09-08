package com.alibou.book.unit.authManagement.controller;

import com.alibou.book.auth.AuthenticationRequest;
import com.alibou.book.auth.AuthenticationResponse;
import com.alibou.book.auth.RegistrationRequest;
import com.alibou.book.auth.controller.AuthenticationController;
import com.alibou.book.auth.service.AuthenticationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
    @Mock
    private AuthenticationService service;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() throws MessagingException {
        // Arrange
        RegistrationRequest request = RegistrationRequest.builder()
                .first_name("John")
                .last_name("Doe")
                .email("john.doe@example.com")
                .password("Password123!")
                .build();

        doNothing().when(service).register(any(RegistrationRequest.class));

        // Act
        authenticationController.register(request);

        // Assert / Verify
        verify(service).register(any(RegistrationRequest.class));
    }

    @Test
    void testAuthenticate() {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("Password123!")
                .build();

        AuthenticationResponse fakeResponse = AuthenticationResponse.builder()
                .token("fake-jwt-token")
                .build();

        when(service.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(fakeResponse);

        // Act
        ResponseEntity<AuthenticationResponse> response = authenticationController.authenticate(request);

        // Assert / Verify
        verify(service).authenticate(any(AuthenticationRequest.class));
        assertEquals(fakeResponse, response.getBody());
    }

    @Test
    void testConfirm() throws MessagingException {
        // Arrange
        String token = "Token";

        doNothing().when(service).activateAccount(token);

        // Act
        authenticationController.confirm(token);

        // Assert / Verify
        verify(service).activateAccount(any(String.class));
    }
}
