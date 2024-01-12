package ee.taltech.iti0302_veebiarendus_backend.user.dto;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;

import java.util.List;

public record LatestLikesResponse(
        List<AlbumSearchDto> likes
) {
}
