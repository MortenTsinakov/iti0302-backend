package ee.taltech.iti0302_veebiarendus_backend.auth.dto;

public record AuthenticationResponse(
        Integer id,
        String username,
        String jwt
        ) {

}