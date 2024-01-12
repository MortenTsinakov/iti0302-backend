package ee.taltech.iti0302_veebiarendus_backend.auth.dto;

public record SignUpRequest(
    String username,
    String password,
    String email
) {}
