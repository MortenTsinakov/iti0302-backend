package ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiAttr(
        @JsonProperty("rank")
        Integer rank
) {}
