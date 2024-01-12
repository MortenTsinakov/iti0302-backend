package ee.taltech.iti0302_veebiarendus_backend.album.repository;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByNameIgnoreCaseAndArtistIgnoreCase(String name, String artist);
    boolean existsByNameIgnoreCaseAndArtistIgnoreCase(String name, String artist);
}
