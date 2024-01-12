package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ApiTracks(
        @JsonProperty("track")
        List<ApiTrack> trackList
) {}
