package ee.taltech.iti0302_veebiarendus_backend.rate.repository;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.rate.entity.Rating;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findRatingByAlbumAndUser(Album album, Object user);
    Long countAllByAlbum(Album album);
    @Query(value = "SELECT SUM(r.score) FROM Rating r WHERE r.album= :album")
    Long sumRatingsByAlbum(@Param("album") Album album);

    List<Rating> findRatingsByUser(User user);
    Page<Rating> findAllByUserIn(List<User> users, Pageable page);
}
