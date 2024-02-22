package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.rate.dto.LatestRatingDto;
import ee.taltech.iti0302_veebiarendus_backend.rate.dto.RatingRequest;
import ee.taltech.iti0302_veebiarendus_backend.rate.dto.RatingResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.rate.entity.Rating;
import ee.taltech.iti0302_veebiarendus_backend.rate.mapper.RatingMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.rate.repository.RatingRepository;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidInputException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidOperationException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.UserNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.rate.service.RateService;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserDto;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.Follow;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.FollowRepository;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @Mock
    private AuthenticationService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    FollowRepository followRepository;
    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private RateService rateService;

    @Test
    void rateAlbum() {
        RatingRequest ratingRequest = new RatingRequest(1L, 5);

        User user = new User();
        Album album = new Album();

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setAlbum(album);
        rating.setScore(ratingRequest.rating());

        ResponseEntity<RatingResponse> expected = ResponseEntity.ok(new RatingResponse(ratingRequest.rating()));

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(ratingRequest.albumId())).thenReturn(Optional.of(album));
        when(ratingMapper.createRating(eq(user), eq(album), eq(ratingRequest.rating()), any(Timestamp.class))).thenReturn(rating);

        ResponseEntity<RatingResponse> result = rateService.rateAlbum(ratingRequest);

        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(ratingRequest.albumId());
        verify(ratingMapper).createRating(eq(user), eq(album), eq(ratingRequest.rating()), any(Timestamp.class));
        verify(ratingRepository).save(argThat(r -> r.getAlbum() == album && r.getUser() == user & r.getScore() == ratingRequest.rating()));

        assertEquals(expected, result);
    }

    @Test
    void rateAlbumAlbumNotFound() {
        RatingRequest ratingRequest = new RatingRequest(1L, 5);
        User user = new User();

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(ratingRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> rateService.rateAlbum(ratingRequest));

        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(ratingRequest.albumId());
    }

    @Test
    void rateAlbumRatingIsNull() {
        RatingRequest ratingRequest = new RatingRequest(1L, null);

        assertThrows(InvalidInputException.class, () -> rateService.rateAlbum(ratingRequest));
    }

    @Test
    void rateAlbumRatingLessThanOne() {
        RatingRequest ratingRequest = new RatingRequest(1L, 0);

        assertThrows(InvalidInputException.class, () -> rateService.rateAlbum(ratingRequest));
    }

    @Test
    void rateAlbumRatingGreaterThanTen() {
        RatingRequest ratingRequest = new RatingRequest(1L, 11);

        assertThrows(InvalidInputException.class, () -> rateService.rateAlbum(ratingRequest));
    }

    @Test
    void updateAlbumRating() {
        RatingRequest ratingRequest = new RatingRequest(1L, 5);
        User user = new User();
        Album album = new Album();
        Rating rating = new Rating();
        rating.setScore(3);
        rating.setAlbum(album);
        rating.setUser(user);

        ResponseEntity<RatingResponse> expected = ResponseEntity.ok(new RatingResponse(ratingRequest.rating()));

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(ratingRequest.albumId())).thenReturn(Optional.of(album));
        when(ratingRepository.findRatingByAlbumAndUser(album, user)).thenReturn(Optional.of(rating));

        ResponseEntity<RatingResponse> actual = rateService.updateAlbumRating(ratingRequest);

        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(ratingRequest.albumId());
        verify(ratingRepository).findRatingByAlbumAndUser(album, user);
        verify(ratingRepository).save(argThat(r -> r.getAlbum() == album && r.getUser() == user && r.getScore() == ratingRequest.rating()));

        assertEquals(expected.getBody().rating(), actual.getBody().rating());
    }

    @Test
    void updateAlbumRatingAlbumNotFound() {
        RatingRequest ratingRequest = new RatingRequest(1L, 5);
        User user = new User();

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(ratingRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> rateService.updateAlbumRating(ratingRequest));

        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(ratingRequest.albumId());
    }

    @Test
    void updateAlbumRatingRatingNotFound() {
        RatingRequest ratingRequest = new RatingRequest(1L, 5);
        User user = new User();
        Album album = new Album();

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(ratingRequest.albumId())).thenReturn(Optional.of(album));
        when(ratingRepository.findRatingByAlbumAndUser(album, user)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> rateService.updateAlbumRating(ratingRequest));

        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(ratingRequest.albumId());
        verify(ratingRepository).findRatingByAlbumAndUser(album, user);
    }

    @Test
    void updateAlbumRatingRatingIsNull() {
        RatingRequest ratingRequest = new RatingRequest(1L, null);

        assertThrows(InvalidInputException.class, () -> rateService.updateAlbumRating(ratingRequest));
    }

    @Test
    void updateAlbumRatingRatingIsLessThanOne() {
        RatingRequest ratingRequest = new RatingRequest(1L, 0);

        assertThrows(InvalidInputException.class, () -> rateService.updateAlbumRating(ratingRequest));
    }

    @Test
    void updateAlbumRatingRatingIsGreaterThanTen() {
        RatingRequest ratingRequest = new RatingRequest(1L, 11);

        assertThrows(InvalidInputException.class, () -> rateService.updateAlbumRating(ratingRequest));
    }

    @Test
    void getUsersLatestRatings() {
        Integer id = 1;
        User user = new User();

        Rating rating1 = new Rating();
        rating1.setTimestamp(Timestamp.from(Instant.parse("2023-02-01T12:00:00Z")));
        Rating rating2 = new Rating();
        rating2.setTimestamp(Timestamp.from(Instant.parse("2023-03-01T12:00:00Z")));
        Rating rating3 = new Rating();
        rating3.setTimestamp(Timestamp.from(Instant.parse("2023-01-01T12:00:00Z")));

        LatestRatingDto latestRating1 = new LatestRatingDto(1, null, new AlbumSearchDto(null, null, null));
        LatestRatingDto latestRating2 = new LatestRatingDto(2, null, new AlbumSearchDto(null, null, null));
        LatestRatingDto latestRating3 = new LatestRatingDto(3, null, new AlbumSearchDto(null, null, null));


        List<Rating> ratingList = List.of(
                rating1,
                rating2,
                rating3
        );

        List<Rating> sortedRatingList = List.of(
                rating2,
                rating1,
                rating3
        );

        List<LatestRatingDto> latestRatingDtoList = List.of(
                latestRating1,
                latestRating2,
                latestRating3
        );

        ResponseEntity<List<LatestRatingDto>> expected = ResponseEntity.ok(latestRatingDtoList);

        when(userRepository.getUserById(id)).thenReturn(Optional.of(user));
        when(ratingRepository.findRatingsByUser(user)).thenReturn(ratingList);
        when(ratingMapper.ratingsToLatestRatingDtoList(sortedRatingList)).thenReturn(latestRatingDtoList);

        ResponseEntity<List<LatestRatingDto>> actual = rateService.getUsersLatestRatings(id);

        verify(userRepository).getUserById(id);
        verify(ratingRepository).findRatingsByUser(user);
        verify(ratingMapper).ratingsToLatestRatingDtoList(sortedRatingList);

        assertEquals(expected, actual);
    }

    @Test
    void getUsersLatestRatingsUserNotFound() {
        Integer id = 1;

        when(userRepository.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> rateService.getUsersLatestRatings(id));
        verify(userRepository).getUserById(id);
    }

    @Test
    void getFriendsLatestRatings() {
        User user = new User();
        User followed = new User();
        Follow follow = new Follow();
        follow.setFollowedId(followed);

        Rating rating1 = new Rating();
        Rating rating2 = new Rating();
        Rating rating3 = new Rating();

        Album album1 = new Album();
        Album album2 = new Album();
        Album album3 = new Album();

        rating1.setAlbum(album1);
        rating2.setAlbum(album2);
        rating3.setAlbum(album3);

        List<User> followedList = List.of(followed);

        rating1.setTimestamp(Timestamp.from(Instant.parse("2023-01-01T12:00:00Z")));
        rating2.setTimestamp(Timestamp.from(Instant.parse("2023-03-01T12:00:00Z")));
        rating3.setTimestamp(Timestamp.from(Instant.parse("2023-02-01T12:00:00Z")));

        List<Rating> ratings = List.of(rating1, rating2, rating3);
        LatestRatingDto dto1 = new LatestRatingDto(5, new UserDto(1, followed.getUsername()), new AlbumSearchDto(rating1.getAlbum().getName(), rating1.getAlbum().getArtist(), rating1.getAlbum().getImageUrl()));
        LatestRatingDto dto2 = new LatestRatingDto(5, new UserDto(1, followed.getUsername()), new AlbumSearchDto(rating2.getAlbum().getName(), rating2.getAlbum().getArtist(), rating2.getAlbum().getImageUrl()));
        LatestRatingDto dto3 = new LatestRatingDto(5, new UserDto(1, followed.getUsername()), new AlbumSearchDto(rating3.getAlbum().getName(), rating3.getAlbum().getArtist(), rating3.getAlbum().getImageUrl()));
        List<LatestRatingDto> ratingDtoList = List.of(dto1, dto2, dto3);

        Page<Rating> likesPage = new PageImpl<>(ratings);

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(followRepository.findAllByFollowerId(user)).thenReturn(List.of(follow));
        when(ratingRepository.findAllByUserIn(eq(followedList), any(Pageable.class))).thenReturn(likesPage);
        when(ratingMapper.ratingsToLatestRatingDtoList(likesPage.getContent())).thenReturn(ratingDtoList);

        ResponseEntity<List<LatestRatingDto>> expected = ResponseEntity.ok(ratingDtoList);
        ResponseEntity<List<LatestRatingDto>> actual = rateService.getFriendsLatestRatings(0);

        verify(authService).getUserFromSecurityContextHolder();
        verify(followRepository).findAllByFollowerId(user);
        verify(ratingRepository).findAllByUserIn(eq(followedList), any(Pageable.class));
        verify(ratingMapper).ratingsToLatestRatingDtoList(likesPage.getContent());

        assertEquals(expected, actual);
    }

    @Test
    void getFriendsLatestRatingsUserNotFound() {
        when(authService.getUserFromSecurityContextHolder()).thenThrow(ClassCastException.class);
        assertThrows(RuntimeException.class, () -> rateService.getFriendsLatestRatings(0));
    }
}