package ee.taltech.iti0302_veebiarendus_backend.album.mapper.albumMapper;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumStatsInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto.AlbumUserInfoDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AlbumInfoMapper {
    AlbumInfoDto albumToAlbumInfoDto(Album album, AlbumUserInfoDto userInfo, AlbumStatsInfoDto stats);
}
