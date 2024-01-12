package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record ApiAlbumMatches(
        @JsonProperty("album")
        ArrayList<ApiAlbumMatch> albumMatchList
) {}
