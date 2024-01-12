package ee.taltech.iti0302_veebiarendus_backend.auth.controller;

import ee.taltech.iti0302_veebiarendus_backend.auth.dto.AuthenticationResponse;
import ee.taltech.iti0302_veebiarendus_backend.auth.dto.LoginRequest;
import ee.taltech.iti0302_veebiarendus_backend.auth.dto.SignUpRequest;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/logout")
    public void logout() {
        authenticationService.logout();
    }

    @PostMapping("/check-auth")
    public ResponseEntity<AuthenticationResponse> checkAuth(@RequestBody String token) {
        return authenticationService.checkAuth(token);
    }

}
