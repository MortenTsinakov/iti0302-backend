package ee.taltech.iti0302_veebiarendus_backend.album.controller;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.LatestLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.LikeRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.LikeResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.likeDto.MyLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.album.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponse> likeAlbum(HttpServletRequest request, @RequestBody LikeRequest likeRequest) {
        return likeService.likeAlbum(request, likeRequest);
    }

    @DeleteMapping
    public ResponseEntity<LikeResponse> unlikeAlbum(HttpServletRequest request, @RequestBody LikeRequest unlikeRequest) {
        return likeService.unlikeAlbum(request, unlikeRequest);
    }

    @GetMapping("/my-likes")
    public ResponseEntity<List<MyLikeDto>> getAllLikedAlbums(HttpServletRequest request) {
        return likeService.getAllLikedAlbums(request);
    }

    @GetMapping("/users-latest")
    public ResponseEntity<List<LatestLikeDto>> getUsersLatestLikes(@RequestParam(name = "user") Integer id) {
        return likeService.getUsersLatestLikes(id);
    }

    @GetMapping("/friends-latest")
    public ResponseEntity<List<LatestLikeDto>> getFriendsLatestLikes(@RequestParam(name="page") Integer page, HttpServletRequest request) {
        return likeService.getFriendsLatestLikes(request, page);
    }
}
