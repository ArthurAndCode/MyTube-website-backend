package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.PageableRequest;
import Arthur.Code.MyTube_website_backend.dto.SearchVideoRequest;
import Arthur.Code.MyTube_website_backend.dto.VideoDTO;
import Arthur.Code.MyTube_website_backend.dto.VideoUploadRequest;
import Arthur.Code.MyTube_website_backend.service.VideoService;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<VideoDTO>> getVideos(@RequestBody PageableRequest request) {
        Page<VideoDTO> videos = videoService.getVideos(request);
        return ResponseEntity.ok(videos);
    }

    @GetMapping( "/users/{id}")
    public ResponseEntity<Page<VideoDTO>> getVideosByUserId(@PathVariable Long id, @RequestBody PageableRequest request) {
        Page<VideoDTO> videos = videoService.getVideosByUserId(id ,request);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VideoDTO>> searchVideosByTitle(@RequestBody SearchVideoRequest request) {
        Page<VideoDTO> videos = videoService.searchVideosByTitle(request);
        return ResponseEntity.ok(videos);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@ModelAttribute VideoUploadRequest request) {
        videoService.uploadVideo(request);
        return ResponseEntity.ok("Video uploaded successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok("Video deleted successfully");
    }
}
