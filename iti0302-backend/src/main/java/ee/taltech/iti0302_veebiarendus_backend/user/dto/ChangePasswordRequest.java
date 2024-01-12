package ee.taltech.iti0302_veebiarendus_backend.user.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
