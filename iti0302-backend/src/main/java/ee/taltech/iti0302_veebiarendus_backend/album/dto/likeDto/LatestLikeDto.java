package ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserDto;

public record LatestLikeDto(UserDto user,
                            AlbumSearchDto album) {
}
