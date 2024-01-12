package ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto;

public record RatingRequest(
        Long albumId,
        Integer rating
) {}
