package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.LatestReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.MyReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewDeleteRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewPostRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Review;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.reviewMapper.ReviewMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.ReviewRepository;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidInputException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidOperationException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.UserNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserDto;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserOnReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.Follow;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.FollowRepository;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    FollowRepository followRepository;
    @Mock
    private AuthenticationService authService;
    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void reviewAlbum() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, "text");
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        Album album = new Album();
        Review review = new Review();
        review.setText(reviewPostRequest.text());
        ReviewResponse response = new ReviewResponse(new UserOnReviewDto(user.getId(), user.getUsername()), reviewPostRequest.text());

        ResponseEntity<ReviewResponse> expected = ResponseEntity.ok(response);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(reviewPostRequest.albumId())).thenReturn(Optional.of(album));
        when(reviewMapper.createReview(eq(user), eq(album), eq(reviewPostRequest.text()), any(Timestamp.class))).thenReturn(review);
        when(reviewMapper.reviewToReviewDto(review)).thenReturn(response);

        ResponseEntity<ReviewResponse> actual = reviewService.reviewAlbum(request, reviewPostRequest);

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(reviewPostRequest.albumId());
        verify(reviewMapper).createReview(eq(user), eq(album), eq(reviewPostRequest.text()), any(Timestamp.class));
        verify(reviewRepository).save(review);
        verify(reviewMapper).reviewToReviewDto(review);

        assertEquals(expected, actual);
    }

    @Test
    void reviewAlbumUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, "text");

        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.reviewAlbum(request, reviewPostRequest));
        verify(authService).getUserFromRequest(request);
    }

    @Test
    void reviewAlbumAlbumNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, "text");
        User user = new User();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(reviewPostRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.reviewAlbum(request, reviewPostRequest));
        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(reviewPostRequest.albumId());
    }

    @Test
    void reviewAlbumReviewLongerThanAllowed() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String text = new Random().ints(256, 33, 126)
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining());
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, text);

        assertThrows(InvalidInputException.class, () -> reviewService.reviewAlbum(request, reviewPostRequest));
    }

    @Test
    void updateReview() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, "text");
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        Album album = new Album();
        Review reviewInitial = new Review();
        reviewInitial.setText("initial");
        Review review = new Review();
        reviewInitial.setText(reviewPostRequest.text());
        ReviewResponse response = new ReviewResponse(new UserOnReviewDto(user.getId(), user.getUsername()), reviewPostRequest.text());

        ResponseEntity<ReviewResponse> expected = ResponseEntity.ok(response);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(reviewPostRequest.albumId())).thenReturn(Optional.of(album));
        when(reviewRepository.findReviewByAlbumAndUser(album, user)).thenReturn(Optional.of(reviewInitial));
        when(reviewMapper.reviewToReviewDto(any())).thenReturn(response);

        ResponseEntity<ReviewResponse> actual = reviewService.updateReview(request, reviewPostRequest);

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(reviewPostRequest.albumId());
        verify(reviewRepository).save(argThat(r -> r.getText().equals(reviewPostRequest.text())));
        verify(reviewMapper).reviewToReviewDto(reviewInitial);

        assertEquals(expected, actual);
    }

    @Test
    void updateReviewUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, "text");

        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.updateReview(request, reviewPostRequest));
        verify(authService).getUserFromRequest(request);
    }

    @Test
    void updateReviewAlbumNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, "text");
        User user =  new User();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(reviewPostRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.updateReview(request, reviewPostRequest));
        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(reviewPostRequest.albumId());
    }

    @Test
    void updateReviewReviewNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, "text");
        User user =  new User();
        Album album = new Album();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(reviewPostRequest.albumId())).thenReturn(Optional.of(album));
        when(reviewRepository.findReviewByAlbumAndUser(album, user)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.updateReview(request, reviewPostRequest));
        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(reviewPostRequest.albumId());
        verify(reviewRepository).findReviewByAlbumAndUser(album, user);
    }

    @Test
    void updateReviewReviewLongerThanAllowed() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String text = new Random().ints(256, 33, 126)
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining());
        ReviewPostRequest reviewPostRequest = new ReviewPostRequest(1L, text);

        assertThrows(InvalidInputException.class, () -> reviewService.updateReview(request, reviewPostRequest));
    }

    @Test
    void deleteReview() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewDeleteRequest deleteRequest = new ReviewDeleteRequest(1L);
        User user = new User();
        Album album = new Album();
        Review review = new Review();
        review.setId(1L);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(deleteRequest.albumId())).thenReturn(Optional.of(album));
        when(reviewRepository.findReviewByAlbumAndUser(album, user)).thenReturn(Optional.of(review));

        reviewService.deleteReview(request, deleteRequest);

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(deleteRequest.albumId());
        verify(reviewRepository).findReviewByAlbumAndUser(album, user);
        verify(reviewRepository).deleteById(review.getId());
    }

    @Test
    void deleteReviewUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewDeleteRequest deleteRequest = new ReviewDeleteRequest(1L);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.deleteReview(request, deleteRequest));
        verify(authService).getUserFromRequest(request);
    }

    @Test
    void deleteReviewAlbumNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewDeleteRequest deleteRequest = new ReviewDeleteRequest(1L);
        User user = new User();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(deleteRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.deleteReview(request, deleteRequest));
        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(deleteRequest.albumId());
    }

    @Test
    void deleteReviewReviewNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReviewDeleteRequest deleteRequest = new ReviewDeleteRequest(1L);
        User user = new User();
        Album album = new Album();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(deleteRequest.albumId())).thenReturn(Optional.of(album));
        when(reviewRepository.findReviewByAlbumAndUser(album, user)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> reviewService.deleteReview(request, deleteRequest));
        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(deleteRequest.albumId());
        verify(reviewRepository).findReviewByAlbumAndUser(album, user);
    }

    @Test
    void getAllUserReviews() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<Review> reviewList = List.of(
            new Review()
        );
        List<MyReviewDto> userReviews = List.of(
            new MyReviewDto("text", new AlbumSearchDto("name", "artist", "url"))
        );

        ResponseEntity<List<MyReviewDto>> expected = ResponseEntity.ok(userReviews);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(reviewRepository.findReviewsByUser(user)).thenReturn(reviewList);
        when(reviewMapper.reviewsToUserReviewDtoList(reviewList)).thenReturn(userReviews);

        ResponseEntity<List<MyReviewDto>> actual = reviewService.getAllUserReviews(request);

        verify(authService).getUserFromRequest(request);
        verify(reviewRepository).findReviewsByUser(user);
        verify(reviewMapper).reviewsToUserReviewDtoList(reviewList);

        assertEquals(expected, actual);
    }

    @Test
    void getAllUserReviewsUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.getAllUserReviews(request));
        verify(authService).getUserFromRequest(request);
    }

    @Test
    void getUsersLatestReviews() {
        Integer id = 1;
        User user = new User();

        Review review1 = new Review();
        review1.setTimestamp(Timestamp.from(Instant.parse("2023-02-01T12:00:00Z")));
        Review review2 = new Review();
        review2.setTimestamp(Timestamp.from(Instant.parse("2023-01-01T12:00:00Z")));
        Review review3 = new Review();
        review3.setTimestamp(Timestamp.from(Instant.parse("2023-03-01T12:00:00Z")));
        List<Review> reviewsInit = List.of(
                review1,
                review2,
                review3
        );
        List<Review> sortedReviews = List.of(review3, review1, review2);
        List<LatestReviewDto> reviews = List.of(
                new LatestReviewDto("text", null, new AlbumSearchDto(null, null, null)),
                new LatestReviewDto("text", null, new AlbumSearchDto(null, null, null)),
                new LatestReviewDto("text", null, new AlbumSearchDto(null, null, null))
        );

        ResponseEntity<List<LatestReviewDto>> expected = ResponseEntity.ok(reviews);

        when(userRepository.getUserById(id)).thenReturn(Optional.of(user));
        when(reviewRepository.findReviewsByUser(user)).thenReturn(reviewsInit);
        when(reviewMapper.reviewsToLatestReviewDtoList(sortedReviews)).thenReturn(reviews);

        ResponseEntity<List<LatestReviewDto>> actual = reviewService.getUsersLatestReviews(id);

        verify(userRepository).getUserById(id);
        verify(reviewRepository).findReviewsByUser(user);
        verify(reviewMapper).reviewsToLatestReviewDtoList(sortedReviews);

        assertEquals(expected, actual);
    }

    @Test
    void getUsersLatestReviewsUserNotFound() {
        Integer id = 1;

        when(userRepository.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.getUsersLatestReviews(id));
        verify(userRepository).getUserById(id);
    }

    @Test
    void getFriendsLatestReviews() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        User followed = new User();
        Follow follow = new Follow();
        follow.setFollowedId(followed);

        Review review1 = new Review();
        Review review2 = new Review();
        Review review3 = new Review();

        Album album1 = new Album();
        Album album2 = new Album();
        Album album3 = new Album();

        review1.setAlbum(album1);
        review2.setAlbum(album2);
        review3.setAlbum(album3);

        List<User> followedList = List.of(followed);

        review1.setTimestamp(Timestamp.from(Instant.parse("2023-01-01T12:00:00Z")));
        review2.setTimestamp(Timestamp.from(Instant.parse("2023-03-01T12:00:00Z")));
        review3.setTimestamp(Timestamp.from(Instant.parse("2023-02-01T12:00:00Z")));

        List<Review> reviews = List.of(review1, review2, review3);
        LatestReviewDto dto1 = new LatestReviewDto("text", new UserDto(1, followed.getUsername()), new AlbumSearchDto(review1.getAlbum().getName(), review1.getAlbum().getArtist(), review1.getAlbum().getImageUrl()));
        LatestReviewDto dto2 = new LatestReviewDto("text", new UserDto(1, followed.getUsername()), new AlbumSearchDto(review2.getAlbum().getName(), review2.getAlbum().getArtist(), review2.getAlbum().getImageUrl()));
        LatestReviewDto dto3 = new LatestReviewDto("text", new UserDto(1, followed.getUsername()), new AlbumSearchDto(review3.getAlbum().getName(), review3.getAlbum().getArtist(), review3.getAlbum().getImageUrl()));
        List<LatestReviewDto> reviewDtoList = List.of(dto1, dto2, dto3);

        Page<Review> reviewPage = new PageImpl<>(reviews);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(followRepository.findAllByFollowerId(user)).thenReturn(List.of(follow));
        when(reviewRepository.findAllByUserIn(eq(followedList), any(Pageable.class))).thenReturn(reviewPage);
        when(reviewMapper.reviewsToLatestReviewDtoList(reviewPage.getContent())).thenReturn(reviewDtoList);

        ResponseEntity<List<LatestReviewDto>> expected = ResponseEntity.ok(reviewDtoList);
        ResponseEntity<List<LatestReviewDto>> actual = reviewService.getFriendsLatestReviews(request, 0);

        verify(authService).getUserFromRequest(request);
        verify(followRepository).findAllByFollowerId(user);
        verify(reviewRepository).findAllByUserIn(eq(followedList), any(Pageable.class));
        verify(reviewMapper).reviewsToLatestReviewDtoList(reviewPage.getContent());

        assertEquals(expected, actual);
    }

    @Test
    void getFriendsReviewsUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reviewService.getFriendsLatestReviews(request, 0));
    }
}