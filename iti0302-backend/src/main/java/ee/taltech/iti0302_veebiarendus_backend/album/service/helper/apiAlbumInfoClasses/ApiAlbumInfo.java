package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.ApiAlbumImage;

import java.util.List;

public record ApiAlbumInfo(
        @JsonProperty("artist")
        String artist,
        @JsonProperty("image")
        List<ApiAlbumImage> imageList,
        @JsonProperty("tracks")
        ApiTracks tracks,
        @JsonProperty("name")
        String name
) {}
