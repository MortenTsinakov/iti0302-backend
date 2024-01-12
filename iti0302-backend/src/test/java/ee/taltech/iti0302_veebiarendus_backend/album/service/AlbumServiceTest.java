package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumStatsInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumUserInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.trackDto.TrackDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Rating;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Review;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Track;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.albumMapper.AlbumInfoMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.albumMapper.AlbumSearchMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.LaterListenRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.LikeRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.RatingRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.ReviewRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.TrackRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.ApiHelper;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.AlbumInfoResult;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.ApiAlbumInfo;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.ApiAlbumMatches;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.ApiResults;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.SearchResults;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.AlbumNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.ApiProcessingException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidInputException;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LaterListenRepository laterListenRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AuthenticationService authService;
    @Mock
    private AlbumInfoMapper albumInfoMapper;
    @Mock
    private AlbumSearchMapper albumSearchMapper;
    @Mock
    private ApiHelper apiHelper;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void searchAlbum() {
        String query = "album";
        Album album = new Album();
        AlbumSearchDto dto = new AlbumSearchDto("", "", "");
        List<Album> albumList = List.of(album);
        List<AlbumSearchDto> albumSearchDtoList = List.of(dto);
        SearchResults results = new SearchResults(new ApiResults(new ApiAlbumMatches(new ArrayList<>())));

        ResponseEntity<List<AlbumSearchDto>> expected = ResponseEntity.ok(albumSearchDtoList);

        when(apiHelper.searchAlbumFromLastFm(query)).thenReturn(results);
        when(apiHelper.createSearchMatchesListFromResponse(results)).thenReturn(albumList);
        when(albumSearchMapper.albumListToAlbumDtoList(albumList)).thenReturn(albumSearchDtoList);

        ResponseEntity<List<AlbumSearchDto>> actual = albumService.searchAlbum(query);

        verify(apiHelper).searchAlbumFromLastFm(query);
        verify(apiHelper).createSearchMatchesListFromResponse(results);
        verify(albumSearchMapper).albumListToAlbumDtoList(albumList);

        assertEquals(expected, actual);
    }

    @Test
    void searchAlbumQueryIsNull() {
        String query = null;
        assertThrows(AlbumNotFoundException.class, () -> albumService.searchAlbum(query));
    }

    @Test
    void searchAlbumQueryIsBlank() {
        String query = "";
        assertThrows(AlbumNotFoundException.class, () -> albumService.searchAlbum(query));
    }

    @Test
    void getAlbumInfoFromDatabase() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = "artist";
        User user = new User();
        Rating rating = new Rating();
        rating.setScore(5);
        Review review = new Review();
        review.setText("text");

        Album album = new Album();
        album.setId(1L);
        album.setName(name);
        album.setArtist(artist);
        AlbumUserInfoDto albumUserInfoDto = new AlbumUserInfoDto(true, true, rating.getScore(), review.getText());
        AlbumStatsInfoDto albumStatsInfoDto = new AlbumStatsInfoDto(0L, 0L);
        AlbumInfoDto albumInfoDto = new AlbumInfoDto(1L, name, artist, "url", null, null, albumUserInfoDto, albumStatsInfoDto);
        ResponseEntity<AlbumInfoDto> expected = ResponseEntity.ok(albumInfoDto);

        when(albumRepository.findByNameIgnoreCaseAndArtistIgnoreCase(name, artist)).thenReturn(Optional.of(album));
        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(likeRepository.existsByAlbumAndUser(album, user)).thenReturn(albumUserInfoDto.like());
        when(laterListenRepository.existsByAlbumAndUser(album, user)).thenReturn(albumUserInfoDto.listenLater());
        when(ratingRepository.findRatingByAlbumAndUser(album, user)).thenReturn(Optional.of(rating));
        when(reviewRepository.findReviewByAlbumAndUser(album, user)).thenReturn(Optional.of(review));
        when(ratingRepository.countAllByAlbum(album)).thenReturn(albumStatsInfoDto.nrOfRatings());
        when(ratingRepository.sumRatingsByAlbum(album)).thenReturn(albumStatsInfoDto.sumOfRatings());
        when(albumInfoMapper.albumToAlbumInfoDto(album, albumUserInfoDto, albumStatsInfoDto)).thenReturn(albumInfoDto);

        ResponseEntity<AlbumInfoDto> result = albumService.getAlbumInfo(request, name, artist);

        verify(albumRepository).findByNameIgnoreCaseAndArtistIgnoreCase(name, artist);
        verify(authService).getUserFromRequest(request);
        verify(likeRepository).existsByAlbumAndUser(album, user);
        verify(laterListenRepository).existsByAlbumAndUser(album, user);
        verify(ratingRepository).findRatingByAlbumAndUser(album, user);
        verify(reviewRepository).findReviewByAlbumAndUser(album, user);
        verify(ratingRepository).countAllByAlbum(album);
        verify(ratingRepository).sumRatingsByAlbum(album);
        verify(albumInfoMapper).albumToAlbumInfoDto(album, albumUserInfoDto, albumStatsInfoDto);

        assertEquals(expected, result);

    }

    @Test
    void getAlbumInfoNameNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = null;
        String artist = "artist";

        assertThrows(InvalidInputException.class, () -> albumService.getAlbumInfo(request, name, artist));
    }

    @Test
    void getAlbumInfoArtistNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = null;

        assertThrows(InvalidInputException.class, () -> albumService.getAlbumInfo(request, name, artist));
    }

    @Test
    void getAlbumInfoNameBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "";
        String artist = "artist";

        assertThrows(InvalidInputException.class, () -> albumService.getAlbumInfo(request, name, artist));
    }

    @Test
    void getAlbumInfoArtistBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = "";

        assertThrows(InvalidInputException.class, () -> albumService.getAlbumInfo(request, name, artist));
    }

    @Test
    void getAlbumInfoAlbumInDatabase() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = "artist";
        User user = new User();
        Rating rating = new Rating();
        rating.setScore(5);
        Review review = new Review();
        review.setText("text");

        Album album = new Album();
        album.setId(1L);
        album.setName(name);
        album.setArtist(artist);
        AlbumUserInfoDto albumUserInfoDto = new AlbumUserInfoDto(true, true, rating.getScore(), review.getText());
        AlbumStatsInfoDto albumStatsInfoDto = new AlbumStatsInfoDto(0L, 0L);
        AlbumInfoDto albumInfoDto = new AlbumInfoDto(1L, name, artist, "url", null, null, albumUserInfoDto, albumStatsInfoDto);
        ResponseEntity<AlbumInfoDto> expected = ResponseEntity.ok(albumInfoDto);

        when(albumRepository.findByNameIgnoreCaseAndArtistIgnoreCase(name, artist)).thenReturn(Optional.of(album));
        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(likeRepository.existsByAlbumAndUser(album, user)).thenReturn(albumUserInfoDto.like());
        when(laterListenRepository.existsByAlbumAndUser(album, user)).thenReturn(albumUserInfoDto.listenLater());
        when(ratingRepository.findRatingByAlbumAndUser(album, user)).thenReturn(Optional.of(rating));
        when(reviewRepository.findReviewByAlbumAndUser(album, user)).thenReturn(Optional.of(review));
        when(ratingRepository.countAllByAlbum(album)).thenReturn(albumStatsInfoDto.nrOfRatings());
        when(ratingRepository.sumRatingsByAlbum(album)).thenReturn(albumStatsInfoDto.sumOfRatings());
        when(albumInfoMapper.albumToAlbumInfoDto(album, albumUserInfoDto, albumStatsInfoDto)).thenReturn(albumInfoDto);

        ResponseEntity<AlbumInfoDto> result = albumService.getAlbumInfo(request, name, artist);

        verify(albumRepository).findByNameIgnoreCaseAndArtistIgnoreCase(name, artist);
        verify(authService).getUserFromRequest(request);
        verify(likeRepository).existsByAlbumAndUser(album, user);
        verify(laterListenRepository).existsByAlbumAndUser(album, user);
        verify(ratingRepository).findRatingByAlbumAndUser(album, user);
        verify(reviewRepository).findReviewByAlbumAndUser(album, user);
        verify(ratingRepository).countAllByAlbum(album);
        verify(ratingRepository).sumRatingsByAlbum(album);
        verify(albumInfoMapper).albumToAlbumInfoDto(album, albumUserInfoDto, albumStatsInfoDto);

        assertEquals(expected, result);
    }

    @Test
    void getAlbumInfoAlbumInDatabaseUserIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = "artist";
        Album album = new Album();
        album.setId(1L);
        album.setName(name);
        album.setArtist(artist);
        AlbumUserInfoDto albumUserInfoDto = new AlbumUserInfoDto(false, false, 0, null);
        AlbumStatsInfoDto albumStatsInfoDto = new AlbumStatsInfoDto(0L, 0L);
        AlbumInfoDto albumInfoDto = new AlbumInfoDto(
                album.getId(),
                album.getName(),
                album.getArtist(),
                null,
                null,
                null,
                albumUserInfoDto,
                albumStatsInfoDto
        );

        ResponseEntity<AlbumInfoDto> expected = ResponseEntity.ok(albumInfoDto);

        when(albumRepository.findByNameIgnoreCaseAndArtistIgnoreCase(name, artist)).thenReturn(Optional.of(album));
        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());
        when(ratingRepository.countAllByAlbum(album)).thenReturn(0L);
        when(ratingRepository.sumRatingsByAlbum(album)).thenReturn(0L);
        when(albumInfoMapper.albumToAlbumInfoDto(album, albumUserInfoDto, albumStatsInfoDto)).thenReturn(albumInfoDto);

        ResponseEntity<AlbumInfoDto> result = albumService.getAlbumInfo(request, name, artist);

        verify(albumRepository).findByNameIgnoreCaseAndArtistIgnoreCase(name, artist);
        verify(authService).getUserFromRequest(request);
        verify(ratingRepository).countAllByAlbum(album);
        verify(ratingRepository).sumRatingsByAlbum(album);
        verify(albumInfoMapper).albumToAlbumInfoDto(album, albumUserInfoDto, albumStatsInfoDto);

        assertEquals(expected, result);
    }

    @Test
    void getAlbumInfoAlbumNotInDatabase() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = "artist";
        AlbumInfoResult apiResponse = new AlbumInfoResult(new ApiAlbumInfo("artist", new ArrayList<>(), null, "name"));

        Album album = new Album();
        album.setId(1L);
        album.setName("n");
        album.setArtist("a");
        Track track = new Track();
        track.setRank(1);
        track.setAlbum(album);
        track.setName("track");
        track.setDuration(1);
        List<Track> trackList = List.of(track);
        List<TrackDto> trackDtoList = List.of(
                new TrackDto(track.getRank(), track.getName(), track.getDuration())
        );

        AlbumUserInfoDto albumUserInfoDto = new AlbumUserInfoDto(false, false, 0, null);
        AlbumInfoDto albumInfoDto = new AlbumInfoDto(
                album.getId(),
                album.getName(),
                album.getArtist(),
                null,
                trackDtoList,
                null,
                albumUserInfoDto,
                null);

        ResponseEntity<AlbumInfoDto> expected = ResponseEntity.ok(albumInfoDto);

        when(albumRepository.findByNameIgnoreCaseAndArtistIgnoreCase(name, artist)).thenReturn(Optional.empty());
        when(apiHelper.getAlbumInfoFromLastFm(name, artist)).thenReturn(apiResponse);
        when(apiHelper.getAlbumFromLastFmResponse(apiResponse)).thenReturn(album);
        when(albumRepository.existsByNameIgnoreCaseAndArtistIgnoreCase(album.getName(), album.getArtist())).thenReturn(false);
        when(apiHelper.getTrackListFromResponse(apiResponse, album)).thenReturn(trackList);
        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());
        when(albumInfoMapper.albumToAlbumInfoDto(album, albumUserInfoDto,null)).thenReturn(albumInfoDto);

        ResponseEntity<AlbumInfoDto> result = albumService.getAlbumInfo(request, name, artist);

        verify(albumRepository).findByNameIgnoreCaseAndArtistIgnoreCase(name, artist);
        verify(apiHelper).getAlbumInfoFromLastFm(name, artist);
        verify(apiHelper).getAlbumFromLastFmResponse(apiResponse);
        verify(albumRepository).existsByNameIgnoreCaseAndArtistIgnoreCase(album.getName(), album.getArtist());
        verify(apiHelper).getTrackListFromResponse(apiResponse, album);
        verify(albumInfoMapper).albumToAlbumInfoDto(album, albumUserInfoDto, null);

        verify(albumRepository).save(argThat(a -> a.getName().equals(album.getName()) && a.getArtist().equals(album.getArtist())));
        verify(trackRepository).saveAll(argThat(t -> t == trackList));

        assertEquals(expected, result);
    }

    @Test
    void getAlbumInfoAlbumNameIsBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = "artist";
        AlbumInfoResult response = new AlbumInfoResult(new ApiAlbumInfo("artist", new ArrayList<>(), null, "name"));;

        Album album = new Album();
        album.setName("");
        album.setArtist(artist);
        List<Track> trackList = List.of(new Track());

        when(albumRepository.findByNameIgnoreCaseAndArtistIgnoreCase(name, artist)).thenReturn(Optional.empty());
        when(apiHelper.getAlbumInfoFromLastFm(name, artist)).thenReturn(response);
        when(apiHelper.getAlbumFromLastFmResponse(response)).thenReturn(album);
        when(albumRepository.existsByNameIgnoreCaseAndArtistIgnoreCase(album.getName(), album.getArtist())).thenReturn(false);
        when(apiHelper.getTrackListFromResponse(response, album)).thenReturn(trackList);

        assertThrows(ApiProcessingException.class, () -> albumService.getAlbumInfo(request, name, artist));

        verify(albumRepository).findByNameIgnoreCaseAndArtistIgnoreCase(name, artist);
        verify(apiHelper).getAlbumInfoFromLastFm(name, artist);
        verify(apiHelper).getAlbumFromLastFmResponse(response);
        verify(albumRepository).existsByNameIgnoreCaseAndArtistIgnoreCase(album.getName(), album.getArtist());
        verify(apiHelper).getTrackListFromResponse(response, album);
    }

    @Test
    void getAlbumInfoArtistIsBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String name = "name";
        String artist = "artist";
        AlbumInfoResult response = new AlbumInfoResult(new ApiAlbumInfo("artist", new ArrayList<>(), null, "name"));;

        Album album = new Album();
        album.setName(name);
        album.setArtist("");
        List<Track> trackList = List.of(new Track());

        when(albumRepository.findByNameIgnoreCaseAndArtistIgnoreCase(name, artist)).thenReturn(Optional.empty());
        when(apiHelper.getAlbumInfoFromLastFm(name, artist)).thenReturn(response);
        when(apiHelper.getAlbumFromLastFmResponse(response)).thenReturn(album);
        when(albumRepository.existsByNameIgnoreCaseAndArtistIgnoreCase(album.getName(), album.getArtist())).thenReturn(false);
        when(apiHelper.getTrackListFromResponse(response, album)).thenReturn(trackList);

        assertThrows(ApiProcessingException.class, () -> albumService.getAlbumInfo(request, name, artist));

        verify(albumRepository).findByNameIgnoreCaseAndArtistIgnoreCase(name, artist);
        verify(apiHelper).getAlbumInfoFromLastFm(name, artist);
        verify(apiHelper).getAlbumFromLastFmResponse(response);
        verify(albumRepository).existsByNameIgnoreCaseAndArtistIgnoreCase(album.getName(), album.getArtist());
        verify(apiHelper).getTrackListFromResponse(response, album);
    }


}