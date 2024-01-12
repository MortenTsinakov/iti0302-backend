package ee.taltech.iti0302_veebiarendus_backend.album.service;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.laterListenDto.LaterListenRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.laterListenDto.LaterListenResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.laterListenDto.MyLaterListenDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.LaterListen;
import ee.taltech.iti0302_veebiarendus_backend.album.mapper.laterListenMapper.LaterListenMapper;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.AlbumRepository;
import ee.taltech.iti0302_veebiarendus_backend.album.repository.LaterListenRepository;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.AlbumNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidOperationException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.UserNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LaterListenServiceTest {

    @Mock
    private AuthenticationService authService;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private LaterListenRepository laterListenRepository;
    @Mock
    private LaterListenMapper laterListenMapper;

    @InjectMocks
    private LaterListenService laterListenService;

    @Test
    void addLaterListen() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LaterListenRequest laterListenRequest = new LaterListenRequest(1L);
        User user = new User();
        Album album = new Album();
        user.setId(1);
        album.setId(1L);
        LaterListen laterListen = new LaterListen();
        laterListen.setId(1L);
        laterListen.setUser(user);
        laterListen.setAlbum(album);

        ResponseEntity<LaterListenResponse> expected = ResponseEntity.ok(new LaterListenResponse(true));

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(laterListenRequest.albumId())).thenReturn(Optional.of(album));
        when(laterListenMapper.createLaterListen(user, album)).thenReturn(laterListen);

        ResponseEntity<LaterListenResponse> result = laterListenService.addLaterListen(request, laterListenRequest);

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(laterListenRequest.albumId());
        verify(laterListenMapper).createLaterListen(user, album);
        verify(laterListenRepository).save(argThat(ll -> ll.getUser() == user && ll.getAlbum() == album));

        assertEquals(expected, result);
    }

    @Test
    void addLaterListenUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LaterListenRequest laterListenRequest = new LaterListenRequest(1L);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> laterListenService.addLaterListen(request, laterListenRequest));

        verify(authService).getUserFromRequest(request);
    }

    @Test
    void addLaterListenAlbumNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LaterListenRequest laterListenRequest = new LaterListenRequest(1L);
        User user = new User();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(laterListenRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(AlbumNotFoundException.class, () -> laterListenService.addLaterListen(request, laterListenRequest));

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(laterListenRequest.albumId());
    }

    @Test
    void removeLaterListen() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LaterListenRequest laterListenRequest = new LaterListenRequest(1L);
        User user = new User();
        user.setId(1);
        Album album = new Album();
        album.setId(1L);
        LaterListen laterListen = new LaterListen();
        laterListen.setId(1L);
        laterListen.setUser(user);
        laterListen.setAlbum(album);

        ResponseEntity<LaterListenResponse> expected = ResponseEntity.ok(new LaterListenResponse(false));

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(laterListenRequest.albumId())).thenReturn(Optional.of(album));
        when(laterListenRepository.findLaterListenByAlbumAndUser(album, user)).thenReturn(Optional.of(laterListen));

        ResponseEntity<LaterListenResponse> result = laterListenService.removeLaterListen(request, laterListenRequest);

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(laterListenRequest.albumId());
        verify(laterListenRepository).findLaterListenByAlbumAndUser(album, user);
        verify(laterListenRepository).deleteById(laterListen.getId());

        assertEquals(expected, result);
    }

    @Test
    void removeLaterListenUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LaterListenRequest laterListenRequest = new LaterListenRequest(1L);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> laterListenService.removeLaterListen(request, laterListenRequest));

        verify(authService).getUserFromRequest(request);
    }

    @Test
    void removeLaterListenAlbumNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LaterListenRequest laterListenRequest = new LaterListenRequest(1L);
        User user = new User();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(laterListenRequest.albumId())).thenReturn(Optional.empty());

        assertThrows(AlbumNotFoundException.class, () -> laterListenService.removeLaterListen(request, laterListenRequest));

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(laterListenRequest.albumId());
    }

    @Test
    void removeLaterListenNotMarked() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        LaterListenRequest laterListenRequest = new LaterListenRequest(1L);
        User user = new User();
        Album album = new Album();

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(albumRepository.findById(laterListenRequest.albumId())).thenReturn(Optional.of(album));
        when(laterListenRepository.findLaterListenByAlbumAndUser(album, user)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> laterListenService.removeLaterListen(request, laterListenRequest));

        verify(authService).getUserFromRequest(request);
        verify(albumRepository).findById(laterListenRequest.albumId());
        verify(laterListenRepository).findLaterListenByAlbumAndUser(album, user);
    }

    @Test
    void getAllLaterListens() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        LaterListen laterListen = new LaterListen();
        Album album = new Album();
        List<Album> albumList = List.of(album);
        laterListen.setAlbum(album);
        List<LaterListen> laterListenList = List.of(laterListen);
        MyLaterListenDto laterListenDto = new MyLaterListenDto("name", "artist", "url");
        List<MyLaterListenDto> laterListenDtoList = List.of(laterListenDto);

        ResponseEntity<List<MyLaterListenDto>> expected = ResponseEntity.ok(laterListenDtoList);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(laterListenRepository.findLaterListenByUser(user)).thenReturn(laterListenList);
        when(laterListenMapper.albumListToMyLaterListenDtoList(List.of(album))).thenReturn(laterListenDtoList);

        ResponseEntity<List<MyLaterListenDto>> result = laterListenService.getAllLaterListens(request);

        verify(authService).getUserFromRequest(request);
        verify(laterListenRepository).findLaterListenByUser(user);
        verify(laterListenMapper).albumListToMyLaterListenDtoList(albumList);

        assertEquals(expected, result);
    }

    @Test
    void getAllLaterListensUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(authService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> laterListenService.getAllLaterListens(request));
        verify(authService).getUserFromRequest(request);
    }
}