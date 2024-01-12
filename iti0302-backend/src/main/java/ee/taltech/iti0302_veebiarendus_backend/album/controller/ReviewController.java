package ee.taltech.iti0302_veebiarendus_backend.album.controller;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.LatestReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.MyReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewDeleteRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewPostRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> reviewAlbum(HttpServletRequest request, @RequestBody ReviewPostRequest reviewRequest) {
        return reviewService.reviewAlbum(request, reviewRequest);
    }

    @PutMapping
    public ResponseEntity<ReviewResponse> updateReview(HttpServletRequest request, @RequestBody ReviewPostRequest updateRequest) {
        return reviewService.updateReview(request, updateRequest);
    }

    @DeleteMapping
    public void deleteReview(HttpServletRequest request, @RequestBody ReviewDeleteRequest deleteRequest) {
        reviewService.deleteReview(request, deleteRequest);
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<MyReviewDto>> getAllUserReviews(HttpServletRequest request) {
        return reviewService.getAllUserReviews(request);
    }


    @GetMapping("/users-latest")
    public ResponseEntity<List<LatestReviewDto>> getUsersLatestReviews(@RequestParam(name = "user") Integer id) {
        return reviewService.getUsersLatestReviews(id);
    }

    @GetMapping("/friends-latest")
    public ResponseEntity<List<LatestReviewDto>> getFriendsReviews(@RequestParam(name = "page") Integer page, HttpServletRequest request) {
        return reviewService.getFriendsLatestReviews(request, page);
    }
}
