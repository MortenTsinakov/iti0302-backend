package ee.taltech.iti0302_veebiarendus_backend.user.repository;

import ee.taltech.iti0302_veebiarendus_backend.user.entity.Follow;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    Optional<Follow> getFollowByFollowerIdAndFollowedId(User follower, User followed);
    boolean existsByFollowerIdAndFollowedId(User follower, User followed);
    List<Follow> findAllByFollowerId(User user);
    Integer countAllByFollowerId(User user);
    Integer countAllByFollowedId(User user);
    Page<Follow> findAllByFollowedId(User user, Pageable pageRequest);
    Page<Follow> findAllByFollowerId(User user, Pageable pageRequest);
}
