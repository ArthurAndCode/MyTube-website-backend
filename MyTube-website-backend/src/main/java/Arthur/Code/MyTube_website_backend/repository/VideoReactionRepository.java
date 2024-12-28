package Arthur.Code.MyTube_website_backend.repository;

import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.model.VideoReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoReactionRepository extends JpaRepository<VideoReaction, Long> {

    List<VideoReaction> findAllByVideoId(Long id);
    Optional<VideoReaction> findByVideoIdAndUserId(Long videoId, Long userId);
    Page<VideoReaction> findAllByUserIdAndLikedTrue(Long userId, Pageable pageable);

}
