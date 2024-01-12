package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlbumInfoResult(
        @JsonProperty("album")
        ApiAlbumInfo album
) {}
