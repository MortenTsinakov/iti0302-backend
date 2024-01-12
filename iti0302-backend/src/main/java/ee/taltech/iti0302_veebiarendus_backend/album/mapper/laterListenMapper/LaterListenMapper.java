package ee.taltech.iti0302_veebiarendus_backend.album.mapper.laterListenMapper;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.laterListenDto.MyLaterListenDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.LaterListen;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LaterListenMapper {
    List<MyLaterListenDto> albumListToMyLaterListenDtoList(List<Album> albumList);
    @Mapping(target = "id", ignore = true)
    LaterListen createLaterListen(User user, Album album);
}
