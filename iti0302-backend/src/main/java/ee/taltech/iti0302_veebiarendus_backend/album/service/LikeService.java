package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.LatestLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.LikeRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.LikeResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.MyLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Like;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.likeMapper.LikeMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.LikeRepository;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.constants.AppConstants;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidOperationException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.UserNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.Follow;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.FollowRepository;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
public class LikeService {

    private final AuthenticationService authService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final AlbumRepository albumRepository;
    private final FollowRepository followRepository;
    private final LikeMapper likeMapper;

    public ResponseEntity<LikeResponse> likeAlbum(HttpServletRequest request, LikeRequest likeRequest) throws InvalidOperationException {
        User user = authService.getUserFromRequest(request).orElseThrow(() -> new InvalidOperationException("Liking album failed: user not found"));
        Album album = albumRepository.findById(likeRequest.albumId()).orElseThrow(() -> new InvalidOperationException("Liking album failed: album not found"));
        Like like = likeMapper.createLike(user, album, Timestamp.from(Instant.now()));
        likeRepository.save(like);
        return ResponseEntity.ok(new LikeResponse(true));
    }

    public ResponseEntity<LikeResponse> unlikeAlbum(HttpServletRequest request, LikeRequest unlikeRequest) throws InvalidOperationException{
        User user = authService.getUserFromRequest(request).orElseThrow(() -> new InvalidOperationException("Unliking album failed: user not found"));
        Album album = albumRepository.findById(unlikeRequest.albumId()).orElseThrow(() -> new InvalidOperationException("Unliking album failed: album not found"));
        Like like = likeRepository.findByAlbumAndUser(album, user).orElseThrow(() -> new InvalidOperationException("Unliking album failed: album is not liked"));
        likeRepository.deleteById(like.getId());
        return ResponseEntity.ok(new LikeResponse(false));
    }

    public ResponseEntity<List<MyLikeDto>> getAllLikedAlbums(HttpServletRequest request) {
        User user = authService.getUserFromRequest(request).orElseThrow(() -> new UserNotFoundException("Fetching liked albums failed: user not found"));
        List<Like> likes = likeRepository.findLikesByUser(user);

        List<MyLikeDto> likedAlbumsList = likeMapper.albumListToMyLikeDtoList(likes.stream()
                .map(Like::getAlbum)
                .toList());
        return ResponseEntity.ok(likedAlbumsList);
    }

    public ResponseEntity<List<LatestLikeDto>> getUsersLatestLikes(Integer id) {
        User user = userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException("Getting users latest likes failed: user not found"));
        List<Album> albums = likeRepository.findLikesByUser(user)
                .stream()
                .sorted(Comparator.comparing(Like::getTimestamp).reversed())
                .limit(AppConstants.LATEST_ALBUMS_LIMIT)
                .map(Like::getAlbum)
                .toList();
        List<LatestLikeDto> likedAlbums = likeMapper.albumListToLatestLikeDtoList(albums);
        return ResponseEntity.ok(likedAlbums);
    }

    public ResponseEntity<List<LatestLikeDto>> getFriendsLatestLikes(HttpServletRequest request, Integer page) {
        User user = authService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Fetching friends latest likes failed: User making the request not found"));
        List<User> followed = followRepository.findAllByFollowerId(user).stream().map(Follow::getFollowedId).toList();

        Sort sort = Sort.by("timestamp").descending();
        Pageable pageRequest = PageRequest.of(page, AppConstants.FRIENDS_PAGE_SIZE, sort);
        Page<Like> likeList = likeRepository.findLikesByUserIn(followed, pageRequest);
        return ResponseEntity.ok(likeMapper.likeListToLatestLikeDtoList(likeList.getContent()));
    }
}
