package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiAlbumImage(
        @JsonProperty("#text")
        String text,
        String size
) {}
