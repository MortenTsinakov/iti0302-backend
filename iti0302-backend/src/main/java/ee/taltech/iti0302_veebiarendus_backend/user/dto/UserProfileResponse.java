package ee.taltech.iti0302_veebiarendus_backend.user.dto;

public record UserProfileResponse(
        String username,
        boolean following
) {
}
