package ee.taltech.iti0302_veebiarendus_backend.auth.service;

import ee.taltech.iti0302_veebiarendus_backend.constants.AppConstants;
import ee.taltech.iti0302_veebiarendus_backend.auth.dto.AuthenticationResponse;
import ee.taltech.iti0302_veebiarendus_backend.auth.dto.LoginRequest;
import ee.taltech.iti0302_veebiarendus_backend.auth.dto.SignUpRequest;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.SignUpValidationException;
import ee.taltech.iti0302_veebiarendus_backend.auth.mapper.SignUpRequestToUserMapper;
import ee.taltech.iti0302_veebiarendus_backend.auth.mapper.UserToAuthResponseMapper;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserToAuthResponseMapper authResponseMapper;
    private final SignUpRequestToUserMapper signUpRequestToUserMapper;

    public ResponseEntity<AuthenticationResponse> signUp(SignUpRequest request) throws SignUpValidationException {
        validateSignUpRequest(request);
        User user = signUpRequestToUserMapper.signUpRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = authResponseMapper.userToAuthResponse(user, jwt);
        return ResponseEntity.ok(authenticationResponse);
    }

    public ResponseEntity<AuthenticationResponse> login(LoginRequest request) throws AuthenticationException {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = userRepository.findByUsername(request.username());
        String jwt = jwtService.generateToken(user);
        AuthenticationResponse authResponse = authResponseMapper.userToAuthResponse(user, jwt);
        return ResponseEntity.ok(authResponse);
    }

    private void validateSignUpRequest(SignUpRequest request) throws SignUpValidationException {
        if (request.username() == null || request.username().isBlank()) {throw new SignUpValidationException("Username is missing.");}
        if (userRepository.existsByUsername(request.username())) {throw new SignUpValidationException("Username already taken");}
        if (request.password() == null) {throw new SignUpValidationException("Password is missing");}
        if (request.password().length() < AppConstants.REQUIRED_PASSWORD_LENGTH) {throw new SignUpValidationException("Password is too short");}
        if (request.email() == null || request.email().isBlank()) {throw new SignUpValidationException("Email is missing");}
        if (userRepository.existsByEmail(request.email())) {throw new SignUpValidationException("User with given email already exists.");}
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public ResponseEntity<AuthenticationResponse> checkAuth(String token) {
        if (token == null) {
            return new ResponseEntity<>(new AuthenticationResponse(null, null, null), HttpStatus.OK);
        }
        if (!jwtService.isTokenExpired(token)) {
            User user = userRepository.findByUsername(jwtService.extractUsername(token));
            return new ResponseEntity<>(authResponseMapper.userToAuthResponse(user, token), HttpStatus.OK);
        }
        return new ResponseEntity<>(new AuthenticationResponse(null, null, null), HttpStatus.OK);
    }

    public Optional<User> getUserFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {return Optional.empty();}
        String jwt = header.substring(7);
        if (jwt.isBlank()) {return Optional.empty();}
        String username = jwtService.extractUsername(jwt);
        if (username != null) {
            User user = userRepository.findByUsername(username);
            if (jwtService.isTokenValid(jwt, user)) {return Optional.of(user);}
        }
        return Optional.empty();
    }
}
