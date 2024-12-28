package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.response.VideoReactionDetailsResponse;
import Arthur.Code.MyTube_website_backend.service.VideoReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos/{videoId}/reactions")
public class VideoReactionController {

    private final VideoReactionService videoReactionService;

    public VideoReactionController(VideoReactionService videoReactionService) {
        this.videoReactionService = videoReactionService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<VideoReactionDetailsResponse> getVideoReactionsDetails(@PathVariable Long videoId, @PathVariable Long userId) {
        VideoReactionDetailsResponse response = videoReactionService.getVideoReactionDetails(videoId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reaction}/users/{userId}")
    public ResponseEntity<String> reactToVideo(@PathVariable Long videoId, @PathVariable String reaction, @PathVariable Long userId) {
        videoReactionService.reactToVideo(videoId, userId, reaction);
        return ResponseEntity.ok().build();
    }

    //VID CONTROLLER
    //getAllLikedVideosByUserId
    //pageable


    //SUB CONTROLLER
    //sub
    //unsub
    //count sub


    //USER CONTROLLER
    //get all subscribed canals
}
