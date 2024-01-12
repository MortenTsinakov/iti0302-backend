package ee.taltech.iti0302_veebiarendus_backend.user.repository;

import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> getUserById(Integer id);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE similarity(username, :searchTerm) > :threshold ORDER BY similarity(username, :searchTerm) DESC")
    List<User> fuzzySearch(@Param("searchTerm") String searchTerm, @Param("threshold") Double threshold);
}
