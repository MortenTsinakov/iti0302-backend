package ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserDto;

public record LatestReviewDto(
        String text,
        UserDto user,
        AlbumSearchDto album
) {
}
