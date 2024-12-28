package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.request.PageableRequest;
import Arthur.Code.MyTube_website_backend.dto.request.SearchVideoRequest;
import Arthur.Code.MyTube_website_backend.dto.response.VideoResponse;
import Arthur.Code.MyTube_website_backend.dto.request.VideoUploadRequest;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.model.VideoReaction;
import Arthur.Code.MyTube_website_backend.repository.VideoReactionRepository;
import Arthur.Code.MyTube_website_backend.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoReactionRepository videoReactionRepository;
    private final UserService userService;
    private final FileService fileService;

    public VideoService(VideoRepository videoRepository, VideoReactionRepository videoReactionRepository, UserService userService, FileService fileService) {
        this.videoRepository = videoRepository;
        this.videoReactionRepository = videoReactionRepository;
        this.userService = userService;
        this.fileService = fileService;
    }

    public Page<VideoResponse> getVideos(PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Video> videos = videoRepository.findAll(pageable);
        return videos.map(this::convertToVideoDTO);
    }

    public Page<VideoResponse> getVideosByUserId(Long id, PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Video> videos = videoRepository.findAllByUserId(id, pageable);
        return videos.map(this::convertToVideoDTO);
    }

    public Page<VideoResponse> searchVideosByTitle(SearchVideoRequest request) {
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

    private Video getVideoByVideoId(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found."));
    }


    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    private VideoResponse convertToVideoDTO(Video video) {
        VideoResponse videoResponse = new VideoResponse();
        videoResponse.setId(video.getId());
        videoResponse.setTitle(video.getTitle());
        videoResponse.setDescription(video.getDescription());
        videoResponse.setThumbnailUrl(userService.composeUrlPath(video.getThumbnailPath()));
        videoResponse.setVideoUrl(userService.composeUrlPath(video.getFilePath()));
        videoResponse.setUser(userService.convertToUserDTO((video.getUser())));
        videoResponse.setCreatedAt(video.getCreatedAt());
        return videoResponse;
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

    public Page<VideoResponse> getLikedVideos(Long userId, PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<VideoReaction> reactions = videoReactionRepository.findAllByUserIdAndLikedTrue(userId, pageable);
        List<Long> likedVideoIds = getVideoIds(reactions);
        Page<Video> likedVideos = videoRepository.findAllByIdIn(likedVideoIds, pageable);
        return likedVideos.map(this::convertToVideoDTO);
    }

    private static List<Long> getVideoIds(Page<VideoReaction> reactions) {
        return reactions.stream()
                .map(reaction -> reaction.getVideo().getId())
                .collect(Collectors.toList());
    }

}
