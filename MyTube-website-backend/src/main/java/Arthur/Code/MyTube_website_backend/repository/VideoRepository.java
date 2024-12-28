package Arthur.Code.MyTube_website_backend.repository;

import Arthur.Code.MyTube_website_backend.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Video> findAllByUserId(Long userId, Pageable pageable);
    Page<Video> findAllByIdIn(List<Long> videoIds, Pageable pageable);


}
