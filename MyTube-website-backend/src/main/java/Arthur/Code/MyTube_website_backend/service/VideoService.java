package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.PageableRequest;
import Arthur.Code.MyTube_website_backend.dto.SearchVideoRequest;
import Arthur.Code.MyTube_website_backend.dto.VideoDTO;
import Arthur.Code.MyTube_website_backend.dto.VideoUploadRequest;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<VideoDTO> getVideos(PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Video> videos = videoRepository.findAll(pageable);
        return videos.map(this::convertToVideoDTO);
    }

    public Page<VideoDTO> getVideosByUserId(Long id, PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Video> videos = videoRepository.findAllByUserId(id, pageable);
        return videos.map(this::convertToVideoDTO);
    }

    public Page<VideoDTO> searchVideosByTitle(SearchVideoRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Video> videos = videoRepository.findByTitleContainingIgnoreCase(request.getTitle(), pageable);
        return videos.map(this::convertToVideoDTO);
    }

    public void uploadVideo(VideoUploadRequest request) {
        User user = userService.getUserById(request.getUserId());
        String videoPath = fileService.saveFile(request.getVideo(), FileService.FileType.VIDEO);
        String thumbnailPath = fileService.saveFile(request.getThumbnail(), FileService.FileType.THUMBNAIL);
        Video video = createVideoEntity(request, user, videoPath, thumbnailPath);
        videoRepository.save(video);
    }

    public void deleteVideo(Long videoId) {
        Video video = getVideoByVideoId(videoId);
        deleteAssociatedFiles(video);
        videoRepository.delete(video);
    }

    private void deleteAssociatedFiles(Video video) {
        try {
            fileService.deleteFile(video.getFilePath());
            fileService.deleteFile(video.getThumbnailPath());
        } catch (FileService.FileStorageException e) {
            throw new RuntimeException("Failed to delete associated files for video");
        }
    }

    protected Video getVideoByVideoId(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found."));
    }


    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
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
