package ee.taltech.iti0302_veebiarendus_backend.album.mapper.trackMapper;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.trackDto.TrackDto;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.Track;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TrackMapper {
    List<TrackDto> tracksToDtoList(List<Track> tracks);
}
