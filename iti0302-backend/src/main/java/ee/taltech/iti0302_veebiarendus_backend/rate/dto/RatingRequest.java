package ee.taltech.iti0302_veebiarendus_backend.rate.dto;

public record RatingRequest(
        Long albumId,
        Integer rating
) {}
