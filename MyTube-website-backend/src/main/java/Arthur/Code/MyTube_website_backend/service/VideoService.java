package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.VideoUploadRequest;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserService userService;
    private final FileService fileService;

    public VideoService(VideoRepository videoRepository, UserService userService, FileService fileService) {
        this.videoRepository = videoRepository;
        this.userService = userService;
        this.fileService = fileService;
    }

    public Page<Video> getSomeVideos(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    public Page<Video> searchVideosByTitle(String title, Pageable pageable) {
        return videoRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public void uploadVideo(VideoUploadRequest request) {
        User user = userService.getUserById(request.getUserId());
        String videoPath = fileService.saveFile(request.getVideo(), FileService.FileType.VIDEO);
        String thumbnailPath = fileService.saveFile(request.getThumbnail(), FileService.FileType.THUMBNAIL);
        Video video = createVideoEntity(request, user, videoPath, thumbnailPath);
        videoRepository.save(video);
    }

    private Video createVideoEntity(VideoUploadRequest request, User user, String videoPath, String thumbnailPath) {
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setUser(user);
        video.setFilePath(videoPath);
        video.setThumbnailPath(thumbnailPath);
        video.setCreatedAt(LocalDateTime.now());
        return video;
    }
}
