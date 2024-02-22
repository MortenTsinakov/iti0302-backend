package ee.taltech.iti0302_veebiarendus_backend.review.dto;

import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserOnReviewDto;

public record ReviewResponse(
        UserOnReviewDto user,
        String text
) {
}
