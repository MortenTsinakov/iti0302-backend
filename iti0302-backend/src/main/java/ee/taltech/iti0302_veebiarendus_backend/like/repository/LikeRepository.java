package ee.taltech.iti0302_veebiarendus_backend.like.repository;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.like.entity.Like;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByAlbumAndUser(Album album, Object user);
    Optional<Like> findByAlbumAndUser(Album album, User user);
    List<Like> findLikesByUser(User user);
    Page<Like> findLikesByUserIn(List<User> users, Pageable page);
}
