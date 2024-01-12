package ee.taltech.iti0302_veebiarendus_backend.album.service.helper;

import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.AlbumInfoResult;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.ApiAlbumInfo;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.ApiAttr;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.ApiTrack;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.ApiTracks;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.ApiAlbumImage;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.ApiAlbumMatch;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.ApiAlbumMatches;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.ApiResults;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.SearchResults;
import ee.taltech.iti0302_veebiarendus_backend.constants.AppConstants;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Track;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.AlbumNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.ApiProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ApiHelper {

    private final WebClient.Builder webClientBuilder;
    private static final String BASE_URL = AppConstants.URL_BASE;
    @Value("${external.api.key}")
    private String apiKey;
    private static final String FORMAT = AppConstants.JSON_FORMAT;
    private static final String ALBUM = "album";

    public ApiHelper(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // SEARCH FOR ALBUM
    public SearchResults searchAlbumFromLastFm(String name) {
        String method = AppConstants.ALBUM_SEARCH_METHOD;
        String limit = AppConstants.LIMIT;

        return webClientBuilder.baseUrl(BASE_URL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", method)
                        .queryParam(ALBUM, name)
                        .queryParam("api_key", apiKey)
                        .queryParam("limit", limit)
                        .queryParam("format", FORMAT)
                        .build())
                .retrieve()
                .bodyToMono(SearchResults.class)
                .block();
    }

    public List<Album> createSearchMatchesListFromResponse(SearchResults response) {
        List<ApiAlbumMatch> matches = Optional.ofNullable(response)
                .map(SearchResults::apiResults)
                .map(ApiResults::albumMatches)
                .map(ApiAlbumMatches::albumMatchList)
                .orElseThrow(() -> new RuntimeException("At least one field in external API response is null"));
        List<Album> albums = new ArrayList<>();
        for (ApiAlbumMatch m: matches) {
            String name = m.name();
            String artist = m.artist();
            String url = "";
            if (m.imageList() != null && !m.imageList().isEmpty()) {
                url = m.imageList().stream()
                        .filter(o -> o.size().equals("extralarge"))
                        .map(ApiAlbumImage::text)
                        .findFirst()
                        .orElse("");
            }
            if (name != null && !name.isBlank() && artist != null && !artist.isBlank()) {
                albums.add(createAlbum(name, artist, url));
            }
        }
        return albums;
    }

    // GET ALBUM INFO

    public AlbumInfoResult getAlbumInfoFromLastFm(String name, String artist) throws AlbumNotFoundException, WebClientResponseException {
        String method = AppConstants.GET_ALBUM_INFO_METHOD;
        String autocorrect = AppConstants.AUTOCORRECT;

        try {
            return webClientBuilder.baseUrl(BASE_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("method", method)
                            .queryParam(ALBUM, name)
                            .queryParam("artist", artist)
                            .queryParam("autocorrect", autocorrect)
                            .queryParam("api_key", apiKey)
                            .queryParam("format", FORMAT)
                            .build())
                    .retrieve()
                    .bodyToMono(AlbumInfoResult.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new AlbumNotFoundException("Couldn't find album: " + name + " by " + artist);
            }
            throw e;
        }
    }

    public Album getAlbumFromLastFmResponse(AlbumInfoResult response) throws AlbumNotFoundException{
        if (response == null) {
            throw new ApiProcessingException("Fetching album info from external API failed: Response is null");
        }

        ApiAlbumInfo albumInfo = Optional.ofNullable(response.album()).orElseThrow(() -> new ApiProcessingException("Fetching album failed: Response doesn't contain ApiAlbumInfo object"));
        String name = albumInfo.name();
        String artist = albumInfo.artist();
        String url = "";
        if (albumInfo.imageList() != null) {
            url = albumInfo.imageList().stream()
                    .filter(o -> o.size().equals("extralarge"))
                    .map(ApiAlbumImage::text)
                    .findFirst()
                    .orElse("");
        }

        if (name == null || name.isBlank() || artist == null || artist.isBlank()) {
            throw new ApiProcessingException("Fetching album info from external API failed: either name of artist is null/blank");
        }

        return createAlbum(name, artist, url);
    }

    public List<Track> getTrackListFromResponse(AlbumInfoResult response, Album album) {
        if (album == null) {throw new ApiProcessingException("Fetching album from external API failed: Album missing in creating track list");}
        List<ApiTrack> apiTrackList = Optional.ofNullable(response)
                .map(AlbumInfoResult::album)
                .map(ApiAlbumInfo::tracks)
                .map(ApiTracks::trackList)
                .orElse(new ArrayList<>());
        List<Track> trackList = new ArrayList<>();
        for (ApiTrack t: apiTrackList) {
            Integer rank = Optional.of(t)
                    .map(ApiTrack::attr)
                    .map(ApiAttr::rank).orElse(null);
            Track track = createTrack(t.name(), t.duration(), rank, album);
            trackList.add(track);
        }
        return trackList;
    }

    // HELPERS

    private Album createAlbum(String name, String artist, String imageUrl) {
        Album album = new Album();
        album.setName(name);
        album.setArtist(artist);
        album.setImageUrl(imageUrl);
        return album;
    }

    private Track createTrack(String trackName, Integer duration, Integer rank, Album album) {
        Track track = new Track();
        track.setRank(rank);
        track.setDuration(duration);
        track.setName(trackName);
        track.setAlbum(album);

        return track;
    }
}
