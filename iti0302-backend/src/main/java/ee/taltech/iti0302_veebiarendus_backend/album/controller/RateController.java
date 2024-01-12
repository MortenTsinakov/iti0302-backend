package ee.taltech.iti0302_veebiarendus_backend.album.controller;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto.RatingRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto.RatingResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.ratingDto.LatestRatingDto;
import ee.taltech.iti0302_veebiarendus_backend.album.service.RateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/rate")
public class RateController {

    private final RateService rateService;

    @PostMapping
    public ResponseEntity<RatingResponse> rateAlbum(HttpServletRequest request, @RequestBody RatingRequest ratingRequest) {
        return rateService.rateAlbum(request, ratingRequest);
    }

    @PutMapping
    public ResponseEntity<RatingResponse> updateAlbumRating(HttpServletRequest request, @RequestBody RatingRequest ratingRequest) {
        return rateService.updateAlbumRating(request, ratingRequest);
    }

    @GetMapping("/users-latest")
    public ResponseEntity<List<LatestRatingDto>> getUsersLatestRatings(@RequestParam(name = "user") Integer id) {
        return rateService.getUsersLatestRatings(id);
    }

    @GetMapping("/friends-latest")
    public ResponseEntity<List<LatestRatingDto>> getFriendsRatings(@RequestParam(name="page") Integer page, HttpServletRequest request) {
        return rateService.getFriendsLatestRatings(request, page);
    }
}
