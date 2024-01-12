package ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto;

public record ReviewPostRequest(
    Long albumId,
    String text
) {}
