package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.review.entity.Review;
import ee.taltech.iti0302_veebiarendus_backend.review.repository.ReviewRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiAlbumInfoClasses.AlbumInfoResult;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.apiSearchClasses.SearchResults;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumStatsInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumUserInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.rate.entity.Rating;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Track;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.AlbumNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.ApiProcessingException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidInputException;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.albumMapper.AlbumInfoMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.albumMapper.AlbumSearchMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.later_listen.repository.LaterListenRepository;
import ee.taltech.iti0302_veebiarendus_backend.like.repository.LikeRepository;
import ee.taltech.iti0302_veebiarendus_backend.rate.repository.RatingRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.TrackRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.service.helper.ApiHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final LikeRepository likeRepository;
    private final LaterListenRepository laterListenRepository;
    private final RatingRepository ratingRepository;
    private final ReviewRepository reviewRepository;
    private final AuthenticationService authService;
    private final AlbumInfoMapper albumInfoMapper;
    private final AlbumSearchMapper albumSearchMapper;
    private final ApiHelper apiHelper;

    public ResponseEntity<List<AlbumSearchDto>> searchAlbum(String name) {
        if (name == null || name.isBlank()) {
            throw new AlbumNotFoundException("Album not found");
        }
        SearchResults searchResponse = apiHelper.searchAlbumFromLastFm(name);
        List<Album> foundAlbums =  apiHelper.createSearchMatchesListFromResponse(searchResponse);
        List<AlbumSearchDto> foundAlbumsDtoList = albumSearchMapper.albumListToAlbumDtoList(foundAlbums);
        return ResponseEntity.ok(foundAlbumsDtoList);
    }

    public ResponseEntity<AlbumInfoDto> getAlbumInfo(String name, String artist) throws AlbumNotFoundException, InvalidInputException, ApiProcessingException {
        if (name == null || artist == null || name.isBlank() || artist.isBlank()) {
            throw new InvalidInputException("Bad Request: One of the inputs is missing");
        }
        try {
            ResponseEntity<AlbumInfoDto> response = getAlbumFromDatabase(name, artist);
            log.debug("Fetched album info from database: {}", name);
            return response;
        } catch (AlbumNotFoundException e) {
            log.debug("Album not found in database, requesting album from Last.fm API: " + name + " by " + artist);
        }
        ResponseEntity<AlbumInfoDto> response = getAlbumInfoFromLastFm(name, artist);
        log.info("Fetched album from Last.fm API");
        return response;
    }

    private ResponseEntity<AlbumInfoDto> getAlbumInfoFromLastFm(String name, String artist) throws AlbumNotFoundException, ApiProcessingException {
        AlbumInfoResult lastFmResponse = apiHelper.getAlbumInfoFromLastFm(name, artist);
        Album album = apiHelper.getAlbumFromLastFmResponse(lastFmResponse);

        /*
        Improve this hack solution! At the moment it's possible that when searching an album from Last.fm the name
        of the artist or the album title will be different from when requesting an album info. For example, when
        searching for term 'nick cave' the artist name for a result will be 'Nick Cave & the Bad Seeds'.
        When requesting album info for this album then the artist name in response will be
        'Nick Cave and the Bad Seeds'. So '&' will be replaced with 'and.
        This causes a problem because we:
        1.
            - Request album from the database by artist name 'Nick Cave & the Bad Seeds'.
            - We actually have the album saved but with the artist name 'Nick Cave and the Bad Seeds'
            - So the album is not fetched from database because when searching with '&' in the name it doesn't exist.
        2.
            - We request the album from Last.fm
            - The artist name is changed to 'Nick Cave and the Bad Seeds'
            - We try to save this album to database but get an error because it already exists.

        So at the moment we check the database twice in case we have requested the album before and it has been saved
        with a slightly different name.
        */
        if (albumRepository.existsByNameIgnoreCaseAndArtistIgnoreCase(album.getName(), album.getArtist())) {
            return getAlbumFromDatabase(album.getName(), album.getArtist());
        }

        List<Track> trackList = apiHelper.getTrackListFromResponse(lastFmResponse, album);
        album.setTrackList(trackList);

        saveAlbumToDatabase(album);
        saveTracksToDatabase(trackList);

        AlbumInfoDto albumDto = albumInfoMapper.albumToAlbumInfoDto(
                album,
                getAlbumUserInfo(album),
                null);

        return ResponseEntity.ok(albumDto);
    }

    private ResponseEntity<AlbumInfoDto> getAlbumFromDatabase(String name, String artist) throws AlbumNotFoundException {
        Album album = albumRepository.findByNameIgnoreCaseAndArtistIgnoreCase(name, artist).orElseThrow(() -> new AlbumNotFoundException("Album not found in database"));
        AlbumInfoDto albumDto = albumInfoMapper.albumToAlbumInfoDto(album, getAlbumUserInfo(album), getAlbumStatsInfo(album));
        return ResponseEntity.ok(albumDto);
    }

    private AlbumUserInfoDto getAlbumUserInfo(Album album) {
        try {
            Object user = authService.getUserFromSecurityContextHolder();
            return new AlbumUserInfoDto(
                    likeRepository.existsByAlbumAndUser(album, user),
                    laterListenRepository.existsByAlbumAndUser(album, user),
                    ratingRepository.findRatingByAlbumAndUser(album, user).map(Rating::getScore).orElse(0),
                    reviewRepository.findReviewByAlbumAndUser(album, user).map(Review::getText).orElse(null)
            );
        } catch (ClassCastException e) {
            return new AlbumUserInfoDto(false, false, 0, null);
        }
    }

    private AlbumStatsInfoDto getAlbumStatsInfo(Album album) {
        return new AlbumStatsInfoDto(
                ratingRepository.countAllByAlbum(album),
                ratingRepository.sumRatingsByAlbum(album)
        );
    }

    private void saveTracksToDatabase(List<Track> trackList) {
        trackRepository.saveAll(trackList);
    }

    private void saveAlbumToDatabase(Album album) throws ApiProcessingException {
        if (album.getName().isBlank() || album.getArtist().isBlank()) {
            throw new ApiProcessingException("Failed to fetch album info");
        }
        albumRepository.save(album);
    }
}
