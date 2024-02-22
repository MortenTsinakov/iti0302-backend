package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.LatestLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.LikeRequest;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.LikeResponse;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.MyLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.like.entity.Like;
import ee.taltech.iti0302_veebiarendus_backend.like.mapper.LikeMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.like.repository.LikeRepository;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidOperationException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.UserNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.like.service.LikeService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private AuthenticationService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    FollowRepository followRepository;
    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    @Test
    void likeAlbum() {
        LikeRequest likeRequest = new LikeRequest(1L);
        User user = new User();
        Album album = new Album();
        Like like = new Like();
        like.setAlbum(album);
        like.setUser(user);
        LikeResponse likeResponse = new LikeResponse(true);

        ResponseEntity<LikeResponse> expected = ResponseEntity.ok(likeResponse);

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(likeRequest.albumId())).thenReturn(Optional.of(album));
        when(likeMapper.createLike(eq(user), eq(album), any(Timestamp.class))).thenReturn(like);

        ResponseEntity<LikeResponse> result = likeService.likeAlbum(likeRequest);

        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(likeRequest.albumId());
        verify(likeMapper).createLike(eq(user), eq(album), any(Timestamp.class));
        verify(likeRepository).save(argThat(l -> l.getAlbum() == album && l.getUser() == user));

        assertEquals(expected, result);
    }

    @Test
    void likeAlbumUserNotFound() {
        LikeRequest likeRequest = new LikeRequest(1L);

        when(authService.getUserFromSecurityContextHolder()).thenReturn(null);

        assertThrows(InvalidOperationException.class, () -> likeService.likeAlbum(likeRequest));
        verify(authService).getUserFromSecurityContextHolder();
    }

    @Test
    void likeAlbumAlbumNotFound() {
        LikeRequest likeRequest = new LikeRequest(1L);
        User user = new User();

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);

        assertThrows(InvalidOperationException.class, () -> likeService.likeAlbum(likeRequest));
        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(likeRequest.albumId());
    }

    @Test
    void unlikeAlbum() {
        LikeRequest likeRequest = new LikeRequest(1L);
        User user = new User();
        user.setId(1);
        Album album = new Album();
        album.setId(1L);
        Like like = new Like();
        like.setId(1L);
        like.setUser(user);
        like.setAlbum(album);

        ResponseEntity<LikeResponse> expected = ResponseEntity.ok(new LikeResponse(false));

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(likeRequest.albumId())).thenReturn(Optional.of(album));
        when(likeRepository.findByAlbumAndUser(album, user)).thenReturn(Optional.of(like));

        ResponseEntity<LikeResponse> result = likeService.unlikeAlbum(likeRequest);

        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(likeRequest.albumId());
        verify(likeRepository).findByAlbumAndUser(album, user);
        verify(likeRepository).deleteById(like.getId());

        assertEquals(expected, result);
    }

    @Test
    void unlikeAlbumUserNotFound() {
        LikeRequest likeRequest = new LikeRequest(1L);

        when(authService.getUserFromSecurityContextHolder()).thenReturn(null);

        assertThrows(InvalidOperationException.class, () -> likeService.unlikeAlbum(likeRequest));
        verify(authService).getUserFromSecurityContextHolder();
    }

    @Test
    void unlikeAlbumAlbumNotFound() {
        LikeRequest likeRequest = new LikeRequest(1L);
        User user = new User();

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(likeRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> likeService.unlikeAlbum(likeRequest));
        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(likeRequest.albumId());
    }

    @Test
    void unlikeAlbumNotPreviouslyLiked() {
        LikeRequest likeRequest = new LikeRequest(1L);
        User user = new User();
        Album album = new Album();

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(albumRepository.findById(likeRequest.albumId())).thenReturn(Optional.of(album));
        when(likeRepository.findByAlbumAndUser(album, user)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> likeService.unlikeAlbum(likeRequest));
        verify(authService).getUserFromSecurityContextHolder();
        verify(albumRepository).findById(likeRequest.albumId());
        verify(likeRepository).findByAlbumAndUser(album, user);
    }

    @Test
    void getAllLikedAlbums() {
        User user = new User();
        Album album = new Album();
        Like like = new Like();
        like.setUser(user);
        like.setAlbum(album);
        List<Like> userLikes = List.of(like);
        List<Album> albumList = List.of(album);
        List<MyLikeDto> albumDtoList = List.of(new MyLikeDto(album.getName(), album.getArtist(), album.getImageUrl()));

        ResponseEntity<List<MyLikeDto>> expected = ResponseEntity.ok(albumDtoList);

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(likeRepository.findLikesByUser(user)).thenReturn(userLikes);
        when(likeMapper.albumListToMyLikeDtoList(albumList)).thenReturn(albumDtoList);

        ResponseEntity<List<MyLikeDto>> result = likeService.getAllLikedAlbums();

        verify(authService).getUserFromSecurityContextHolder();
        verify(likeRepository).findLikesByUser(user);
        verify(likeMapper).albumListToMyLikeDtoList(albumList);

        assertEquals(expected, result);
    }

    @Test
    void getUsersLatestLikes() {
        Integer id = 1;
        User user = new User();
        Album album1 = new Album();
        Album album2 = new Album();
        Album album3 = new Album();
        album1.setName("album1");
        album2.setName("album2");
        album3.setName("album3");
        Like like1 = new Like();
        Like like2 = new Like();
        Like like3 = new Like();
        like1.setAlbum(album1);
        like2.setAlbum(album2);
        like3.setAlbum(album3);
        like1.setTimestamp(Timestamp.from(Instant.parse("2023-03-01T12:00:00Z")));
        like2.setTimestamp(Timestamp.from(Instant.parse("2023-05-01T12:00:00Z")));
        like3.setTimestamp(Timestamp.from(Instant.parse("2023-04-01T12:00:00Z")));

        List<Like> likes = List.of(like1, like2, like3);
        List<Album> albums = List.of(album2, album3, album1);
        List<LatestLikeDto> response = List.of(
                new LatestLikeDto(new UserDto(user.getId(), user.getUsername()), new AlbumSearchDto(album2.getName(), album2.getArtist(), album2.getImageUrl())),
                new LatestLikeDto(new UserDto(user.getId(), user.getUsername()), new AlbumSearchDto(album3.getName(), album3.getArtist(), album3.getImageUrl())),
                new LatestLikeDto(new UserDto(user.getId(), user.getUsername()), new AlbumSearchDto(album1.getName(), album1.getArtist(), album1.getImageUrl()))
        );

        ResponseEntity<List<LatestLikeDto>> expected = ResponseEntity.ok(response);

        when(userRepository.getUserById(id)).thenReturn(Optional.of(user));
        when(likeRepository.findLikesByUser(user)).thenReturn(likes);
        when(likeMapper.albumListToLatestLikeDtoList(albums)).thenReturn(response);

        ResponseEntity<List<LatestLikeDto>> result = likeService.getUsersLatestLikes(id);

        verify(userRepository).getUserById(id);
        verify(likeRepository).findLikesByUser(user);
        verify(likeMapper).albumListToLatestLikeDtoList(albums);

        assertEquals(expected, result);
    }

    @Test
    void getUsersLatestLikesUserNotFound() {
        Integer id = 1;

        when(userRepository.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> likeService.getUsersLatestLikes(id));
        verify(userRepository).getUserById(id);
    }

    @Test
    void getFriendsLatestLikes() {
        User user = new User();
        User followed = new User();
        Follow follow = new Follow();
        follow.setFollowedId(followed);

        Like like1 = new Like();
        Like like2 = new Like();
        Like like3 = new Like();

        Album album1 = new Album();
        Album album2 = new Album();
        Album album3 = new Album();

        like1.setAlbum(album1);
        like2.setAlbum(album2);
        like3.setAlbum(album3);

        List<User> followedList = List.of(followed);

        like1.setTimestamp(Timestamp.from(Instant.parse("2023-01-01T12:00:00Z")));
        like2.setTimestamp(Timestamp.from(Instant.parse("2023-03-01T12:00:00Z")));
        like3.setTimestamp(Timestamp.from(Instant.parse("2023-02-01T12:00:00Z")));

        List<Like> likes = List.of(like1, like2, like3);
        LatestLikeDto dto1 = new LatestLikeDto(new UserDto(1, followed.getUsername()), new AlbumSearchDto(like1.getAlbum().getName(), like1.getAlbum().getArtist(), like1.getAlbum().getImageUrl()));
        LatestLikeDto dto2 = new LatestLikeDto(new UserDto(1, followed.getUsername()), new AlbumSearchDto(like2.getAlbum().getName(), like2.getAlbum().getArtist(), like2.getAlbum().getImageUrl()));
        LatestLikeDto dto3 = new LatestLikeDto(new UserDto(1, followed.getUsername()), new AlbumSearchDto(like3.getAlbum().getName(), like3.getAlbum().getArtist(), like3.getAlbum().getImageUrl()));
        List<LatestLikeDto> likesDtoList = List.of(dto1, dto2, dto3);

        Page<Like> likesPage = new PageImpl<>(likes);

        when(authService.getUserFromSecurityContextHolder()).thenReturn(user);
        when(followRepository.findAllByFollowerId(user)).thenReturn(List.of(follow));
        when(likeRepository.findLikesByUserIn(eq(followedList), any(Pageable.class))).thenReturn(likesPage);
        when(likeMapper.likeListToLatestLikeDtoList(likesPage.getContent())).thenReturn(likesDtoList);

        ResponseEntity<List<LatestLikeDto>> expected = ResponseEntity.ok(likesDtoList);
        ResponseEntity<List<LatestLikeDto>> actual = likeService.getFriendsLatestLikes(0);

        verify(authService).getUserFromSecurityContextHolder();
        verify(followRepository).findAllByFollowerId(user);
        verify(likeRepository).findLikesByUserIn(eq(followedList), any(Pageable.class));
        verify(likeMapper).likeListToLatestLikeDtoList(likesPage.getContent());

        assertEquals(expected, actual);
    }

    @Test
    void getFriendsLatestLikesUserNotFound() {
        when(authService.getUserFromSecurityContextHolder()).thenThrow(ClassCastException.class);

        assertThrows(RuntimeException.class, () -> likeService.getFriendsLatestLikes(0));
    }
}