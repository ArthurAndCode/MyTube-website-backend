package Arthur.Code.MyTube_website_backend.repository;

import Arthur.Code.MyTube_website_backend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByUserId(Long id);
    Optional<PasswordResetToken> findByToken(String token);

}
