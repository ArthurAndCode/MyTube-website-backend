package Arthur.Code.MyTube_website_backend.repository;

import Arthur.Code.MyTube_website_backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<Object> findByUsername(String username);
    Optional<User> findByRememberMe(String token);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.rememberMe = :token WHERE u.id = :id")
    void updateRememberMeToken(@Param("id") Long id, @Param("token") String token);
}
