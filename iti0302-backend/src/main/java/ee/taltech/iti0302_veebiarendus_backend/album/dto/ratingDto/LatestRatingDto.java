package ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserDto;

public record LatestRatingDto(
    Integer score,
    UserDto user,
    AlbumSearchDto album
) {
}
