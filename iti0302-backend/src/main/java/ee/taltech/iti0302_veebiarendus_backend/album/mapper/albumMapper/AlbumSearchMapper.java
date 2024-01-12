package ee.taltech.iti0302_veebiarendus_backend.album.mapper.albumMapper;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumSearchDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AlbumSearchMapper {
    List<AlbumSearchDto> albumListToAlbumDtoList(List<Album> albumList);
}
