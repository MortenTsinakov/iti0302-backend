package ee.taltech.iti0302_veebiarendus_backend.review.dto;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;

public record MyReviewDto(
    String text,
    AlbumSearchDto album
) {}
