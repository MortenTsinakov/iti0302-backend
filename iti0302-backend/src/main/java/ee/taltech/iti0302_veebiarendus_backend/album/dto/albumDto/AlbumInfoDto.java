package ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto;

import ee.taltech.iti0302_veebiarendus_backend.review.dto.ReviewResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.trackDto.TrackDto;

import java.util.List;

public record AlbumInfoDto(
        Long id,
        String name,
        String artist,
        String imageUrl,
        List<TrackDto> trackList,
        List<ReviewResponse> reviews,
        AlbumUserInfoDto userInfo,
        AlbumStatsInfoDto stats
) {}
