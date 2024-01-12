package ee.taltech.iti0302_veebiarendus_backend.auth.dto;

public record LoginRequest(
   String username,
   String password
) {}
