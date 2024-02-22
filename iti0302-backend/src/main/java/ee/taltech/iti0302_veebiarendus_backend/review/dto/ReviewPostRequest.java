package ee.taltech.iti0302_veebiarendus_backend.review.dto;

public record ReviewPostRequest(
    Long albumId,
    String text
) {}
