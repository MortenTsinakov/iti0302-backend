package ee.taltech.iti0302_veebiarendus_backend.album.controller;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.album.service.AlbumService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/album")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/info")
    public ResponseEntity<AlbumInfoDto> getAlbumInfo(HttpServletRequest request,
                                                     @RequestParam(name = "album") String album,
                                                     @RequestParam(name = "artist") String artist) {
        return albumService.getAlbumInfo(request, album, artist);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AlbumSearchDto>> searchAlbum(@RequestParam(name = "album") String album) {
        return albumService.searchAlbum(album);
    }
}
