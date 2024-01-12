package ee.taltech.iti0302_veebiarendus_backend.user.dto;

public record ChangeUsernameResponse(
        Integer id,
        String username,
        String jwt
) {
}
