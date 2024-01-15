package ee.taltech.iti0302_veebiarendus_backend.album.repository;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.album.entity.LaterListen;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaterListenRepository extends JpaRepository<LaterListen, Long> {
    Optional<LaterListen> findLaterListenByAlbumAndUser(Album album, User user);
    List<LaterListen> findLaterListenByUser(User user);
    boolean existsByAlbumAndUser(Album album, Object user);
}
