package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto.RatingRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto.RatingResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto.LatestRatingDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Rating;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.ratingMapper.RatingMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.RatingRepository;
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
public class RateService {

    private final AuthenticationService authService;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final RatingRepository ratingRepository;
    private final FollowRepository followRepository;
    private final RatingMapper ratingMapper;

    public ResponseEntity<RatingResponse> rateAlbum(RatingRequest ratingRequest) throws InvalidOperationException, InvalidInputException {
        validateRating(ratingRequest.rating());
        User user = authService.getUserFromSecurityContextHolder();
        Album album = albumRepository.findById(ratingRequest.albumId()).orElseThrow(() -> new InvalidOperationException("Rating album failed: album not found"));
        Rating rating = ratingMapper.createRating(user, album, ratingRequest.rating(), Timestamp.from(Instant.now()));
        ratingRepository.save(rating);
        return ResponseEntity.ok(new RatingResponse(rating.getScore()));
    }

    public ResponseEntity<RatingResponse> updateAlbumRating(RatingRequest ratingRequest) throws InvalidOperationException {
        validateRating(ratingRequest.rating());
        User user = authService.getUserFromSecurityContextHolder();
        Album album = albumRepository.findById(ratingRequest.albumId()).orElseThrow(() -> new InvalidOperationException("Updating rating failed: album not found"));
        Rating rating = ratingRepository.findRatingByAlbumAndUser(album, user).orElseThrow(() -> new InvalidOperationException("Updating rating failed: rating not found"));
        rating.setScore(ratingRequest.rating());
        rating.setTimestamp(Timestamp.from(Instant.now()));
        ratingRepository.save(rating);
        return ResponseEntity.ok(new RatingResponse(rating.getScore()));
    }

    private void validateRating(Integer rating) throws InvalidInputException {
        if (rating == null || rating <= 0 || rating > 10) {throw new InvalidInputException("Rating failed: invalid rating");}
    }

    public ResponseEntity<List<LatestRatingDto>> getUsersLatestRatings(Integer id) {
        User user = userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException("Getting users latest ratings failed: user not found"));
        List<LatestRatingDto> ratings = ratingMapper.ratingsToLatestRatingDtoList(ratingRepository.findRatingsByUser(user)
                .stream()
                .sorted(Comparator.comparing(Rating::getTimestamp).reversed())
                .limit(AppConstants.LATEST_ALBUMS_LIMIT)
                .toList()
        );
        return ResponseEntity.ok(ratings);
    }

    public ResponseEntity<List<LatestRatingDto>> getFriendsLatestRatings(Integer page) {
        User user = authService.getUserFromSecurityContextHolder();
        List<User> followed = followRepository.findAllByFollowerId(user).stream().map(Follow::getFollowedId).toList();

        Sort sort = Sort.by("timestamp").descending();
        Pageable pageRequest = PageRequest.of(page, AppConstants.FRIENDS_PAGE_SIZE, sort);
        Page<Rating> ratings = ratingRepository.findAllByUserIn(followed, pageRequest);
        return ResponseEntity.ok(ratingMapper.ratingsToLatestRatingDtoList(ratings.getContent()));
    }
}
