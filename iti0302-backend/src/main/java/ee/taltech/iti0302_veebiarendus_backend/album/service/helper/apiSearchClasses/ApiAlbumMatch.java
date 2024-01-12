package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ApiAlbumMatch(
        @JsonProperty("name")
        String name,
        @JsonProperty("artist")
        String artist,
        @JsonProperty("image")
        List<ApiAlbumImage> imageList
) {}
