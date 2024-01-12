package ee.taltech.iti0302_veebiarendus_backend.album.mapper.reviewMapper;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.LatestReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.MyReviewDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.reviewDto.ReviewResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Review;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    ReviewResponse reviewToReviewDto(Review review);
    List<ReviewResponse> reviewsToReviewDtoList(List<Review> reviews);
    List<MyReviewDto> reviewsToUserReviewDtoList(List<Review> reviews);
    List<LatestReviewDto> reviewsToLatestReviewDtoList(List<Review> reviews);
    @Mapping(target = "id", ignore = true)
    Review createReview(User user, Album album, String text, Timestamp timestamp);
}
