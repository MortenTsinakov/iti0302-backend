package ee.taltech.iti0302_veebiarendus_backend.album.repository;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findAllByAlbumId(Long albumId);
}
