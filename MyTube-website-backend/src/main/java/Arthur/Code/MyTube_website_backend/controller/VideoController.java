package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.VideoUploadRequest;
import Arthur.Code.MyTube_website_backend.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@ModelAttribute VideoUploadRequest request) {
        videoService.uploadVideo(request);
        return ResponseEntity.ok("Video uploaded successfully");
    }

}
