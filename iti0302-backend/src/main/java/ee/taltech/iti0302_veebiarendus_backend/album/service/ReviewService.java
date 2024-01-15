package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.LatestReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.MyReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewDeleteRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewPostRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Review;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.reviewMapper.ReviewMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.ReviewRepository;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.constants.AppConstants;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidInputException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidOperationException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.UserNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.Follow;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.FollowRepository;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final AlbumRepository albumRepository;
    private final FollowRepository followRepository;
    private final AuthenticationService authService;
    private final ReviewMapper reviewMapper;

    public ResponseEntity<ReviewResponse> reviewAlbum(ReviewPostRequest reviewRequest) throws InvalidOperationException, InvalidInputException {
        validateReview(reviewRequest);
        User user = authService.getUserFromSecurityContextHolder();
        Album album = albumRepository.findById(reviewRequest.albumId()).orElseThrow(() -> new InvalidOperationException("Album not found"));
        Review review = reviewMapper.createReview(user, album, reviewRequest.text(), Timestamp.from(Instant.now()));
        reviewRepository.save(review);
        return ResponseEntity.ok(reviewMapper.reviewToReviewDto(review));
    }

    public ResponseEntity<ReviewResponse> updateReview(ReviewPostRequest reviewRequest) throws InvalidOperationException{
        validateReview(reviewRequest);
        User user = authService.getUserFromSecurityContextHolder();
        Album album = albumRepository.findById(reviewRequest.albumId()).orElseThrow(() -> new InvalidOperationException("Updating a review failed: album not found"));
        Review review = reviewRepository.findReviewByAlbumAndUser(album, user).orElseThrow(() -> new InvalidOperationException("Updating review failed: review not found"));
        review.setText(reviewRequest.text());
        review.setTimestamp(Timestamp.from(Instant.now()));
        reviewRepository.save(review);
        return ResponseEntity.ok(reviewMapper.reviewToReviewDto(review));
    }

    public void deleteReview(ReviewDeleteRequest deleteRequest) throws InvalidOperationException {
        User user = authService.getUserFromSecurityContextHolder();
        Album album = albumRepository.findById(deleteRequest.albumId()).orElseThrow(() -> new InvalidOperationException("Deleting review failed: album not found"));
        Review review = reviewRepository.findReviewByAlbumAndUser(album, user).orElseThrow(() -> new InvalidOperationException("Deleting review failed: review not found"));
        reviewRepository.deleteById(review.getId());
    }

    private void validateReview(ReviewPostRequest reviewRequest) throws InvalidInputException {
        if (reviewRequest.text().length() > 255) {throw new InvalidInputException("Review is too long");}
    }

    public ResponseEntity<List<MyReviewDto>> getAllUserReviews() throws UserNotFoundException {
        User user = authService.getUserFromSecurityContextHolder();
        List<Review> reviews = reviewRepository.findReviewsByUser(user);
        return ResponseEntity.ok(reviewMapper.reviewsToUserReviewDtoList(reviews));
    }

    public ResponseEntity<List<LatestReviewDto>> getUsersLatestReviews(Integer id) {
        User user = userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException("Getting users latest reviews failed: user not found"));
        List<LatestReviewDto> reviews = reviewMapper.reviewsToLatestReviewDtoList(reviewRepository.findReviewsByUser(user)
                .stream()
                .sorted(Comparator.comparing(Review::getTimestamp).reversed())
                .limit(AppConstants.LATEST_ALBUMS_LIMIT)
                .toList());
        return ResponseEntity.ok(reviews);
    }

    public ResponseEntity<List<LatestReviewDto>> getFriendsLatestReviews(Integer page) {
        User user = authService.getUserFromSecurityContextHolder();
        List<User> followed = followRepository.findAllByFollowerId(user).stream().map(Follow::getFollowedId).toList();

        Sort sort = Sort.by("timestamp").descending();
        Pageable pageRequest = PageRequest.of(page, AppConstants.FRIENDS_PAGE_SIZE, sort);
        Page<Review> reviewPage = reviewRepository.findAllByUserIn(followed, pageRequest);
        return ResponseEntity.ok(reviewMapper.reviewsToLatestReviewDtoList(reviewPage.getContent()));
    }
}
