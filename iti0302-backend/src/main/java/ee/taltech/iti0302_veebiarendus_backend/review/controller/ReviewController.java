package ee.taltech.iti0302_veebiarendus_backend.review.controller;

import ee.taltech.iti0302_veebiarendus_backend.review.dto.LatestReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.review.dto.MyReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.review.dto.ReviewDeleteRequest;
import ee.taltech.iti0302_veebiarendus_backend.review.dto.ReviewResponse;
import ee.taltech.iti0302_veebiarendus_backend.review.dto.ReviewPostRequest;
import ee.taltech.iti0302_veebiarendus_backend.review.service.ReviewService;
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
    public ResponseEntity<ReviewResponse> reviewAlbum(@RequestBody ReviewPostRequest reviewRequest) {
        return reviewService.reviewAlbum(reviewRequest);
    }

    @PutMapping
    public ResponseEntity<ReviewResponse> updateReview(@RequestBody ReviewPostRequest updateRequest) {
        return reviewService.updateReview(updateRequest);
    }

    @DeleteMapping
    public void deleteReview(@RequestBody ReviewDeleteRequest deleteRequest) {
        reviewService.deleteReview(deleteRequest);
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<MyReviewDto>> getAllUserReviews() {
        return reviewService.getAllUserReviews();
    }


    @GetMapping("/users-latest")
    public ResponseEntity<List<LatestReviewDto>> getUsersLatestReviews(@RequestParam(name = "user") Integer id) {
        return reviewService.getUsersLatestReviews(id);
    }

    @GetMapping("/friends-latest")
    public ResponseEntity<List<LatestReviewDto>> getFriendsReviews(@RequestParam(name = "page") Integer page) {
        return reviewService.getFriendsLatestReviews(page);
    }
}
