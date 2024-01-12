package ee.taltech.iti0302_veebiarendus_backend.album.service.helper;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Track;
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
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.AlbumNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.ApiProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApiHelperTest {

    @InjectMocks
    ApiHelper apiHelper;

    @Test
    void createSearchMatchesListFromResponse() {
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        List<ApiAlbumImage> imageList = List.of(image);
        ApiAlbumMatch match = new ApiAlbumMatch("name", "artist", imageList);
        ArrayList<ApiAlbumMatch> matchList = new ArrayList<>(List.of(match));
        ApiAlbumMatches albumMatches = new ApiAlbumMatches(matchList);
        ApiResults apiResults = new ApiResults(albumMatches);
        SearchResults response = new SearchResults(apiResults);
        Album album = new Album();
        album.setName("name");
        album.setArtist("artist");
        album.setImageUrl("url");

        List<Album> expected = List.of(album);
        List<Album> actual = apiHelper.createSearchMatchesListFromResponse(response);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getArtist(), actual.get(i).getArtist());
            assertEquals(expected.get(i).getImageUrl(), actual.get(i).getImageUrl());
        }
    }

    @Test
    void createSearchMatchesListFromResponseResponseIsNull() {
        SearchResults response = null;
        assertThrows(RuntimeException.class, () -> apiHelper.createSearchMatchesListFromResponse(response));
    }

    @Test
    void createSearchMatchesListFromResponseApiResultsIsNull() {
        SearchResults response = new SearchResults(null);
        assertThrows(RuntimeException.class, () -> apiHelper.createSearchMatchesListFromResponse(response));
    }

    @Test
    void createSearchMatchesListFromResponseAlbumMatchesIsNull() {
        SearchResults response = new SearchResults(new ApiResults(null));
        assertThrows(RuntimeException.class, () -> apiHelper.createSearchMatchesListFromResponse(response));
    }

    @Test
    void createSearchMatchesListFromResponseAlbumMatchListIsNull() {
        SearchResults response = new SearchResults(new ApiResults(new ApiAlbumMatches(null)));
        assertThrows(RuntimeException.class, () -> apiHelper.createSearchMatchesListFromResponse(response));
    }

    @Test
    void createSearchMatchesListFromResponseImageOfCorrectSizeIsMissing() {
        ApiAlbumImage image = new ApiAlbumImage("url", "small");
        List<ApiAlbumImage> imageList = List.of(image);
        ApiAlbumMatch match = new ApiAlbumMatch("name", "artist", imageList);
        ArrayList<ApiAlbumMatch> matchList = new ArrayList<>(List.of(match));
        ApiAlbumMatches albumMatches = new ApiAlbumMatches(matchList);
        ApiResults apiResults = new ApiResults(albumMatches);
        SearchResults response = new SearchResults(apiResults);
        Album album = new Album();
        album.setName("name");
        album.setArtist("artist");
        album.setImageUrl("");

        List<Album> expected = List.of(album);
        List<Album> actual = apiHelper.createSearchMatchesListFromResponse(response);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getArtist(), actual.get(i).getArtist());
            assertEquals(expected.get(i).getImageUrl(), actual.get(i).getImageUrl());
        }
    }

    @Test
    void createSearchMatchesListFromResponseNameIsNull() {
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        List<ApiAlbumImage> imageList = List.of(image);
        ApiAlbumMatch match1 = new ApiAlbumMatch(null, "artist", imageList);
        ApiAlbumMatch match2 = new ApiAlbumMatch("name", "artist", imageList);
        ArrayList<ApiAlbumMatch> matchList = new ArrayList<>(List.of(match1, match2));
        ApiAlbumMatches albumMatches = new ApiAlbumMatches(matchList);
        ApiResults apiResults = new ApiResults(albumMatches);
        SearchResults response = new SearchResults(apiResults);
        Album album = new Album();
        album.setName("name");
        album.setArtist("artist");
        album.setImageUrl("url");

        List<Album> expected = List.of(album);
        List<Album> actual = apiHelper.createSearchMatchesListFromResponse(response);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getArtist(), actual.get(i).getArtist());
            assertEquals(expected.get(i).getImageUrl(), actual.get(i).getImageUrl());
        }
    }

    @Test
    void createSearchMatchesListFromResponseNameIsBlank() {
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        List<ApiAlbumImage> imageList = List.of(image);
        ApiAlbumMatch match1 = new ApiAlbumMatch("", "artist", imageList);
        ApiAlbumMatch match2 = new ApiAlbumMatch("name", "artist", imageList);
        ArrayList<ApiAlbumMatch> matchList = new ArrayList<>(List.of(match1, match2));
        ApiAlbumMatches albumMatches = new ApiAlbumMatches(matchList);
        ApiResults apiResults = new ApiResults(albumMatches);
        SearchResults response = new SearchResults(apiResults);
        Album album = new Album();
        album.setName("name");
        album.setArtist("artist");
        album.setImageUrl("url");

        List<Album> expected = List.of(album);
        List<Album> actual = apiHelper.createSearchMatchesListFromResponse(response);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getArtist(), actual.get(i).getArtist());
            assertEquals(expected.get(i).getImageUrl(), actual.get(i).getImageUrl());
        }
    }

    @Test
    void createSearchMatchesListFromResponseArtistIsNull() {
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        List<ApiAlbumImage> imageList = List.of(image);
        ApiAlbumMatch match1 = new ApiAlbumMatch("name", null, imageList);
        ApiAlbumMatch match2 = new ApiAlbumMatch("name", "artist", imageList);
        ArrayList<ApiAlbumMatch> matchList = new ArrayList<>(List.of(match1, match2));
        ApiAlbumMatches albumMatches = new ApiAlbumMatches(matchList);
        ApiResults apiResults = new ApiResults(albumMatches);
        SearchResults response = new SearchResults(apiResults);
        Album album = new Album();
        album.setName("name");
        album.setArtist("artist");
        album.setImageUrl("url");

        List<Album> expected = List.of(album);
        List<Album> actual = apiHelper.createSearchMatchesListFromResponse(response);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getArtist(), actual.get(i).getArtist());
            assertEquals(expected.get(i).getImageUrl(), actual.get(i).getImageUrl());
        }
    }
    @Test
    void createSearchMatchesListFromResponseArtistIsBlank() {
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        List<ApiAlbumImage> imageList = List.of(image);
        ApiAlbumMatch match1 = new ApiAlbumMatch("name", "", imageList);
        ApiAlbumMatch match2 = new ApiAlbumMatch("name", "artist", imageList);
        ArrayList<ApiAlbumMatch> matchList = new ArrayList<>(List.of(match1, match2));
        ApiAlbumMatches albumMatches = new ApiAlbumMatches(matchList);
        ApiResults apiResults = new ApiResults(albumMatches);
        SearchResults response = new SearchResults(apiResults);
        Album album = new Album();
        album.setName("name");
        album.setArtist("artist");
        album.setImageUrl("url");

        List<Album> expected = List.of(album);
        List<Album> actual = apiHelper.createSearchMatchesListFromResponse(response);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getArtist(), actual.get(i).getArtist());
            assertEquals(expected.get(i).getImageUrl(), actual.get(i).getImageUrl());
        }
    }


    @Test
    void getAlbumFromLastFmLastFmResponse() {
        ApiAttr attr = new ApiAttr(1);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = "name";
        String artist = "artist";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        Album expected = new Album();
        expected.setName(name);
        expected.setArtist(artist);
        expected.setImageUrl("url");

        Album actual = apiHelper.getAlbumFromLastFmResponse(response);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
    }

    @Test
    void getAlbumFromLastFmLastFmResponseResponseIsNull() {
        assertThrows(ApiProcessingException.class, () -> apiHelper.getAlbumFromLastFmResponse(null));
    }

    @Test
    void getAlbumFromLastFmLastFmResponseApiAlbumInfoIsNull() {
        AlbumInfoResult response = new AlbumInfoResult(null);
        assertThrows(ApiProcessingException.class, () -> apiHelper.getAlbumFromLastFmResponse(null));
    }

    @Test
    void getAlbumFromLastFmLastFmResponseImageListIsNull() {
        ApiAttr attr = new ApiAttr(1);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = null;
        String name = "name";
        String artist = "artist";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        Album expected = new Album();
        expected.setName(name);
        expected.setArtist(artist);
        expected.setImageUrl("");

        Album actual = apiHelper.getAlbumFromLastFmResponse(response);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
    }

    @Test
    void getAlbumFromLastFmLastFmResponseNameIsNull() {
        ApiAttr attr = new ApiAttr(1);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = null;
        String artist = "artist";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        assertThrows(ApiProcessingException.class, () -> apiHelper.getAlbumFromLastFmResponse(response));
    }

    @Test
    void getAlbumFromLastFmLastFmResponseNameIsBlank() {
        ApiAttr attr = new ApiAttr(1);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = "";
        String artist = "artist";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        assertThrows(ApiProcessingException.class, () -> apiHelper.getAlbumFromLastFmResponse(response));
    }

    @Test
    void getAlbumFromLastFmLastFmResponseArtistIsNull() {
        ApiAttr attr = new ApiAttr(1);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = "name";
        String artist = null;
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        assertThrows(ApiProcessingException.class, () -> apiHelper.getAlbumFromLastFmResponse(response));
    }

    @Test
    void getAlbumFromLastFmLastFmResponseArtistIsBlank() {
        ApiAttr attr = new ApiAttr(1);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = "name";
        String artist = "";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        assertThrows(ApiProcessingException.class, () -> apiHelper.getAlbumFromLastFmResponse(response));
    }

    @Test
    void getTrackListFromResponse() {
        ApiAttr attr = new ApiAttr(1);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = "name";
        String artist = "artist";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        Album album = new Album();

        Track expectedTrack = new Track();
        expectedTrack.setAlbum(album);
        expectedTrack.setRank(1);
        expectedTrack.setDuration(1);
        expectedTrack.setName("1");

        List<Track> expected = List.of(expectedTrack);
        List<Track> actual = apiHelper.getTrackListFromResponse(response, album);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getRank(), actual.get(i).getRank());
            assertEquals(expected.get(i).getDuration(), actual.get(i).getDuration());
            assertEquals(expected.get(i).getAlbum(), actual.get(i).getAlbum());
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
        }
    }

    @Test
    void getTrackListFromResponseResponseIsNull() {
        Album album = new Album();
        assertTrue(apiHelper.getTrackListFromResponse(null, album).isEmpty());
    }

    @Test
    void getTrackListFromResponseApiAlbumInfoIsNull() {
        AlbumInfoResult response = new AlbumInfoResult(null);
        Album album = new Album();
        assertTrue(apiHelper.getTrackListFromResponse(response, album).isEmpty());
    }

    @Test
    void getTrackListFromResponseApiTracksIsNull() {
        AlbumInfoResult response = new AlbumInfoResult(new ApiAlbumInfo("artist", null, null, "name"));
        Album album = new Album();
        assertTrue(apiHelper.getTrackListFromResponse(response, album).isEmpty());
    }

    @Test
    void getTrackListFromResponseTrackListIsNull() {
        AlbumInfoResult response = new AlbumInfoResult(new ApiAlbumInfo("artist", null, new ApiTracks(null), "name"));
        Album album = new Album();
        assertTrue(apiHelper.getTrackListFromResponse(response, album).isEmpty());
    }

    @Test
    void getTrackListFromResponseAlbumIsNull() {
        AlbumInfoResult response = new AlbumInfoResult(null);
        assertThrows(ApiProcessingException.class, () -> apiHelper.getTrackListFromResponse(response, null));
    }

    @Test
    void getTrackListFromResponseAttrIsNull() {
        ApiAttr attr = null;
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = "name";
        String artist = "artist";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        Album album = new Album();
        Track expectedTrack = new Track();
        expectedTrack.setAlbum(album);
        expectedTrack.setName("1");
        expectedTrack.setDuration(1);
        expectedTrack.setRank(null);

        List<Track> expected = List.of(expectedTrack);
        List<Track> actual = apiHelper.getTrackListFromResponse(response, album);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getRank(), actual.get(i).getRank());
            assertEquals(expected.get(i).getDuration(), actual.get(i).getDuration());
            assertEquals(expected.get(i).getAlbum(), actual.get(i).getAlbum());
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
        }
    }

    @Test
    void getTrackListFromResponseRankIsNull() {
        ApiAttr attr = new ApiAttr(null);
        ApiTrack track1 = new ApiTrack(1, "1", attr);
        List<ApiTrack> trackList = List.of(track1);
        ApiAlbumImage image = new ApiAlbumImage("url", "extralarge");
        ApiTracks apiTracks = new ApiTracks(trackList);
        List<ApiAlbumImage> imageList = List.of(image);
        String name = "name";
        String artist = "artist";
        ApiAlbumInfo albumInfo = new ApiAlbumInfo(artist, imageList, apiTracks, name);
        AlbumInfoResult response = new AlbumInfoResult(albumInfo);

        Album album = new Album();
        Track expectedTrack = new Track();
        expectedTrack.setAlbum(album);
        expectedTrack.setName("1");
        expectedTrack.setDuration(1);
        expectedTrack.setRank(null);

        List<Track> expected = List.of(expectedTrack);
        List<Track> actual = apiHelper.getTrackListFromResponse(response, album);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getRank(), actual.get(i).getRank());
            assertEquals(expected.get(i).getDuration(), actual.get(i).getDuration());
            assertEquals(expected.get(i).getAlbum(), actual.get(i).getAlbum());
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
        }
    }
}