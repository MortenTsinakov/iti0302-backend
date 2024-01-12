package ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;

public record MyReviewDto(
    String text,
    AlbumSearchDto album
) {}
