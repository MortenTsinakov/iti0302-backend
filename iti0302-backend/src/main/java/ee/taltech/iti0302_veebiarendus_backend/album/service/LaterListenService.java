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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class LaterListenService {

    private final AuthenticationService authService;
    private final AlbumRepository albumRepository;
    private final LaterListenRepository laterListenRepository;
    private final LaterListenMapper laterListenMapper;

    public ResponseEntity<LaterListenResponse> addLaterListen(HttpServletRequest request, LaterListenRequest laterListenRequest) throws UserNotFoundException, AlbumNotFoundException {
        User user = authService.getUserFromRequest(request).orElseThrow(() -> new UserNotFoundException("Later listen not added: user not found"));
        Album album = albumRepository.findById(laterListenRequest.albumId()).orElseThrow(() -> new AlbumNotFoundException("Later listen not added: album not found"));
        LaterListen laterListen = laterListenMapper.createLaterListen(user, album);
        laterListenRepository.save(laterListen);
        return ResponseEntity.ok(new LaterListenResponse(true));
    }

    public ResponseEntity<LaterListenResponse> removeLaterListen(HttpServletRequest request, LaterListenRequest removeLaterListenRequest) throws UserNotFoundException, AlbumNotFoundException {
        User user = authService.getUserFromRequest(request).orElseThrow(() -> new UserNotFoundException("Later listen not removed: user not found"));
        Album album = albumRepository.findById(removeLaterListenRequest.albumId()).orElseThrow(() -> new AlbumNotFoundException("Later listen not removed: album not found"));
        LaterListen laterListen = laterListenRepository.findLaterListenByAlbumAndUser(album, user).orElseThrow(() -> new InvalidOperationException("Later listen not removed: album hasn't been marked for later listening"));
        laterListenRepository.deleteById(laterListen.getId());
        return ResponseEntity.ok(new LaterListenResponse(false));
    }

    public ResponseEntity<List<MyLaterListenDto>> getAllLaterListens(HttpServletRequest request) {
        User user = authService.getUserFromRequest(request).orElseThrow(() -> new UserNotFoundException("Failed to fetch later listens: user not found"));
        List<LaterListen> laterListens = laterListenRepository.findLaterListenByUser(user);
        List<MyLaterListenDto> laterListensDtoList = laterListenMapper.albumListToMyLaterListenDtoList(laterListens.stream()
                .map(LaterListen::getAlbum)
                .toList());
        return ResponseEntity.ok(laterListensDtoList);
    }
}
