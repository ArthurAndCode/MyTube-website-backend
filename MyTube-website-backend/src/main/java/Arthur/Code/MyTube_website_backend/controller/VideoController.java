package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.VideoUploadRequest;
import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.service.VideoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<Page<VideoDTO>> getSomeVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<VideoDTO> videos = videoService.getSomeVideos(pageable);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VideoDTO>> searchVideosByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<VideoDTO> videos = videoService.searchVideosByTitle(title, pageable);
        return ResponseEntity.ok(videos);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@ModelAttribute VideoUploadRequest request) {
        videoService.uploadVideo(request);
        return ResponseEntity.ok("Video uploaded successfully");
    }

}
