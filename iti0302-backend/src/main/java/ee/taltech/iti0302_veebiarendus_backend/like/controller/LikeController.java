package ee.taltech.iti0302_veebiarendus_backend.like.controller;

import ee.taltech.iti0302_veebiarendus_backend.like.dto.LatestLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.LikeRequest;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.LikeResponse;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.MyLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.like.service.LikeService;
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
    public ResponseEntity<LikeResponse> likeAlbum(@RequestBody LikeRequest likeRequest) {
        return likeService.likeAlbum(likeRequest);
    }

    @DeleteMapping
    public ResponseEntity<LikeResponse> unlikeAlbum(@RequestBody LikeRequest unlikeRequest) {
        return likeService.unlikeAlbum(unlikeRequest);
    }

    @GetMapping("/my-likes")
    public ResponseEntity<List<MyLikeDto>> getAllLikedAlbums() {
        return likeService.getAllLikedAlbums();
    }

    @GetMapping("/users-latest")
    public ResponseEntity<List<LatestLikeDto>> getUsersLatestLikes(@RequestParam(name = "user") Integer id) {
        return likeService.getUsersLatestLikes(id);
    }

    @GetMapping("/friends-latest")
    public ResponseEntity<List<LatestLikeDto>> getFriendsLatestLikes(@RequestParam(name="page") Integer page) {
        return likeService.getFriendsLatestLikes(page);
    }
}
