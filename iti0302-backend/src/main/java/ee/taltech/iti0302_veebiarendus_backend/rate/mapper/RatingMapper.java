package ee.taltech.iti0302_veebiarendus_backend.rate.mapper;

import ee.taltech.iti0302_veebiarendus_backend.rate.dto.LatestRatingDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.rate.entity.Rating;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {
    List<LatestRatingDto> ratingsToLatestRatingDtoList(List<Rating> ratings);
    @Mapping(target = "id", ignore = true)
    Rating createRating(User user, Album album, Integer score, Timestamp timestamp);
}
