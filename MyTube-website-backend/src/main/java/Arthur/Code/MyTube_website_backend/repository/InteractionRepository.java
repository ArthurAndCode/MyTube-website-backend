package Arthur.Code.MyTube_website_backend.repository;

import Arthur.Code.MyTube_website_backend.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
}
