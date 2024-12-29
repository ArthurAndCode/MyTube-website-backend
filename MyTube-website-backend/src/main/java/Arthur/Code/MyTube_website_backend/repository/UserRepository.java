package Arthur.Code.MyTube_website_backend.repository;

import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.model.Video;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<Object> findByUsername(String username);
    Optional<User> findByRememberMe(String token);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<User> findAllByIdIn(List<Long> videoIds, Pageable pageable);
}
