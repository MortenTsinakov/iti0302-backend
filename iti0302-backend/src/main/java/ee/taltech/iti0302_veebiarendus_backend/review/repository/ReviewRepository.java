package ee.taltech.iti0302_veebiarendus_backend.review.repository;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Album;
import ee.taltech.iti0302_veebiarendus_backend.review.entity.Review;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository()
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findReviewByAlbumAndUser(Album album, Object user);
    List<Review> findReviewsByUser(User user);
    void deleteByAlbumAndUser(Album album, User user);
    Page<Review> findAllByUserIn(List<User> users, Pageable page);
}
