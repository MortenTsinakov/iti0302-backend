package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiTrack(
        @JsonProperty("duration")
        Integer duration,
        @JsonProperty("name")
        String name,
        @JsonProperty("@attr")
        ApiAttr attr
) {}
