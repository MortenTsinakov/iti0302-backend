package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResults(
        @JsonProperty("albummatches")
        ApiAlbumMatches albumMatches
) {}
