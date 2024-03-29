package ee.taltech.iti0302_veebiarendus_backend.like.mapper;

import ee.taltech.iti0302_veebiarendus_backend.like.dto.LatestLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.like.dto.MyLikeDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.like.entity.Like;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    List<MyLikeDto> albumListToMyLikeDtoList(List<Album> albumList);
    List<LatestLikeDto> albumListToLatestLikeDtoList(List<Album> albumList);
    List<LatestLikeDto> likeListToLatestLikeDtoList(List<Like> likeList);
    @Mapping(target = "id", ignore = true)
    Like createLike(User user, Album album, Timestamp timestamp);
}
