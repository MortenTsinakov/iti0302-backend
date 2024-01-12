package ee.taltech.iti0302_veebiarendus_backend.auth.service;

import ee.taltech.iti0302_veebiarendus_backend.auth.dto.AuthenticationResponse;
import ee.taltech.iti0302_veebiarendus_backend.auth.dto.LoginRequest;
import ee.taltech.iti0302_veebiarendus_backend.auth.dto.SignUpRequest;
import ee.taltech.iti0302_veebiarendus_backend.auth.mapper.SignUpRequestToUserMapper;
import ee.taltech.iti0302_veebiarendus_backend.auth.mapper.UserToAuthResponseMapper;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.SignUpValidationException;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private UserToAuthResponseMapper authResponseMapper;
    @Mock
    private SignUpRequestToUserMapper signUpRequestToUserMapper;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthenticationService authService;

    @Test
    void signUp() {
        SignUpRequest request = new SignUpRequest("username", "password", "email@email.com");
        User user = new User();
        String jwt = "jwtToken";
        AuthenticationResponse response = new AuthenticationResponse(1, request.username(), jwt);

        ResponseEntity<AuthenticationResponse> expected = ResponseEntity.ok(response);

        when(signUpRequestToUserMapper.signUpRequestToUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(jwtService.generateToken(user)).thenReturn(jwt);
        when(authResponseMapper.userToAuthResponse(user, jwt)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> actual = authService.signUp(request);

        verify(signUpRequestToUserMapper).signUpRequestToUser(request);
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(user);
        verify(jwtService).generateToken(user);
        verify(authResponseMapper).userToAuthResponse(user, jwt);

        assertEquals(expected, actual);
    }

    @Test
    void signUpUsernameIsNull() {
        SignUpRequest request = new SignUpRequest(null, "password", "email@email.com");
        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
    }

    @Test
    void signUpUsernameIsBlank() {
        SignUpRequest request = new SignUpRequest("", "password", "email@email.com");
        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
    }

    @Test
    void signUpUserAlreadyExists() {
        SignUpRequest request = new SignUpRequest("username", "password", "email@email.com");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
        verify(userRepository).existsByUsername(request.username());
    }

    @Test
    void signUpPasswordIsNull() {
        SignUpRequest request = new SignUpRequest("username", null, "email@email.com");
        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
    }

    @Test
    void signUpPasswordIsTooShort() {
        SignUpRequest request = new SignUpRequest("username", "pass", "email@email.com");
        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
    }

    @Test
    void signUpEmailIsNull() {
        SignUpRequest request = new SignUpRequest("username", "password", null);
        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
    }

    @Test
    void signUpEmailIsBlank() {
        SignUpRequest request = new SignUpRequest("username", "password", "");
        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
    }

    @Test
    void signUpUserWithGivenEmailAlreadyExists() {
        SignUpRequest request = new SignUpRequest("username", "password", "email@email.com");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(SignUpValidationException.class, () -> authService.signUp(request));
        verify(userRepository).existsByEmail(request.email());
    }

    @Test
    void login() {
        LoginRequest request = new LoginRequest("username", "password");
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        String jwt = "jwt";
        AuthenticationResponse authResponse = new AuthenticationResponse(user.getId(), user.getUsername(), jwt);

        ResponseEntity<AuthenticationResponse> expected = ResponseEntity.ok(authResponse);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername(request.username())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwt);
        when(authResponseMapper.userToAuthResponse(user, jwt)).thenReturn(authResponse);

        ResponseEntity<AuthenticationResponse> actual = authService.login(request);

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(user.getUsername());
        verify(jwtService).generateToken(user);
        verify(authResponseMapper).userToAuthResponse(user, jwt);

        assertEquals(expected, actual);
    }

    @Test
    void checkAuth() {
        String token = "jwt";
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        AuthenticationResponse response = new AuthenticationResponse(user.getId(), user.getUsername(), token);
        ResponseEntity<AuthenticationResponse> expected = ResponseEntity.ok(response);

        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractUsername(token)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(authResponseMapper.userToAuthResponse(user, token)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> actual = authService.checkAuth(token);

        verify(jwtService).isTokenExpired(token);
        verify(jwtService).extractUsername(token);
        verify(userRepository).findByUsername(user.getUsername());
        verify(authResponseMapper).userToAuthResponse(user, token);

        assertEquals(expected, actual);
    }

    @Test
    void checkAuthTokenIsNull() {
        AuthenticationResponse response = new AuthenticationResponse(null, null, null);
        ResponseEntity<AuthenticationResponse> expected = ResponseEntity.ok(response);

        ResponseEntity<AuthenticationResponse> actual = authService.checkAuth(null);

        assertEquals(expected, actual);
    }

    @Test
    void checkAuthTokenHasExpired() {
        String token = "jwt";
        AuthenticationResponse response = new AuthenticationResponse(null, null, null);
        ResponseEntity<AuthenticationResponse> expected = ResponseEntity.ok(response);

        when(jwtService.isTokenExpired(token)).thenReturn(true);

        ResponseEntity<AuthenticationResponse> actual = authService.checkAuth(token);

        verify(jwtService).isTokenExpired(token);
        assertEquals(expected, actual);
    }

    @Test
    void getUserFromRequest() {
        String jwt = "jwt";
        User user = new User();
        user.setUsername("username");

        Optional<User> expected = Optional.of(user);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid(jwt, user)).thenReturn(true);

        Optional<User> actual = authService.getUserFromRequest(request);

        verify(request).getHeader("Authorization");
        verify(jwtService).extractUsername(jwt);
        verify(userRepository).findByUsername(user.getUsername());
        verify(jwtService).isTokenValid(jwt, user);

        assertEquals(expected, actual);
    }

    @Test
    void getUserFromRequestHeaderIsNull() {
        String jwt = "jwt";
        Optional<User> expected = Optional.empty();

        when(request.getHeader("Authorization")).thenReturn(null);

        Optional<User> actual = authService.getUserFromRequest(request);

        verify(request).getHeader("Authorization");
        assertEquals(expected, actual);
    }

    @Test
    void getUserFromRequestHeaderIsBlank() {
        String jwt = "jwt";
        Optional<User> expected = Optional.empty();

        when(request.getHeader("Authorization")).thenReturn("");

        Optional<User> actual = authService.getUserFromRequest(request);

        verify(request).getHeader("Authorization");
        assertEquals(expected, actual);
    }

    @Test
    void getUserFromRequestJwtIsBlank() {
        String jwt = "";
        Optional<User> expected = Optional.empty();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

        Optional<User> actual = authService.getUserFromRequest(request);

        verify(request).getHeader("Authorization");
        assertEquals(expected, actual);
    }

    @Test
    void getUserFromRequestUsernameIsNull() {
        String jwt = "jwt";
        Optional<User> expected = Optional.empty();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(null);

        Optional<User> actual = authService.getUserFromRequest(request);

        verify(jwtService).extractUsername(jwt);
        verify(request).getHeader("Authorization");
        assertEquals(expected, actual);
    }

    @Test
    void getUserFromRequestTokenIsNotValid() {
        String jwt = "jwt";
        Optional<User> expected = Optional.empty();
        User user = new User();
        user.setUsername("username");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid(jwt, user)).thenReturn(false);

        Optional<User> actual = authService.getUserFromRequest(request);

        verify(request).getHeader("Authorization");
        verify(jwtService).extractUsername(jwt);
        verify(userRepository).findByUsername(user.getUsername());
        verify(jwtService).isTokenValid(jwt, user);
        assertEquals(expected, actual);
    }
}