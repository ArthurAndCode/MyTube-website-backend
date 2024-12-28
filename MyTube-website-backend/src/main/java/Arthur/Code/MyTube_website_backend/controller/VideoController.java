package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.request.PageableRequest;
import Arthur.Code.MyTube_website_backend.dto.request.SearchVideoRequest;
import Arthur.Code.MyTube_website_backend.dto.response.VideoResponse;
import Arthur.Code.MyTube_website_backend.dto.request.VideoUploadRequest;
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
    public ResponseEntity<Page<VideoResponse>> getVideos(@RequestBody PageableRequest request) {
        Page<VideoResponse> videos = videoService.getVideos(request);
        return ResponseEntity.ok(videos);
    }

    @GetMapping( "/users/{id}")
    public ResponseEntity<Page<VideoResponse>> getVideosByUserId(@PathVariable Long id, @RequestBody PageableRequest request) {
        Page<VideoResponse> videos = videoService.getVideosByUserId(id ,request);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VideoResponse>> searchVideosByTitle(@RequestBody SearchVideoRequest request) {
        Page<VideoResponse> videos = videoService.searchVideosByTitle(request);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/users/{id}/liked")
    public ResponseEntity<Page<VideoResponse>> getLikedVideos(@PathVariable Long id, @RequestBody PageableRequest request) {
        Page<VideoResponse> videos = videoService.getLikedVideos(id, request);
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
