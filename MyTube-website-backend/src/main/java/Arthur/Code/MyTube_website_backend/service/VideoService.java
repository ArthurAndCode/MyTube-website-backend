package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.VideoDTO;
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

    public Page<VideoDTO> getSomeVideos(Pageable pageable) {
        Page<Video> videos = videoRepository.findAll(pageable);
        return videos.map(this::convertToVideoDTO);
    }

    public Page<VideoDTO> searchVideosByTitle(String title, Pageable pageable) {
        Page<Video> videos = videoRepository.findByTitleContainingIgnoreCase(title, pageable);
        return videos.map(this::convertToVideoDTO);
    }

    private VideoDTO convertToVideoDTO(Video video) {
        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setId(video.getId());
        videoDTO.setTitle(video.getTitle());
        videoDTO.setDescription(video.getDescription());
        videoDTO.setThumbnailUrl(userService.composeUrlPath(video.getThumbnailPath()));
        videoDTO.setVideoUrl(userService.composeUrlPath(video.getFilePath()));
        videoDTO.setUser(userService.convertToUserDTO((video.getUser())));
        videoDTO.setCreatedAt(video.getCreatedAt());
        return videoDTO;
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
