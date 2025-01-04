package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.service.CommentService;
import Arthur.Code.MyTube_website_backend.service.UserService;
import Arthur.Code.MyTube_website_backend.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('MODERATOR')")
@RequestMapping("/api/v1/moderator")
public class ModeratorController {

    private final UserService userService;
    private final CommentService commentService;
    private final VideoService videoService;

    public ModeratorController(UserService userService, CommentService commentService, VideoService videoService) {
        this.userService = userService;
        this.commentService = commentService;
        this.videoService = videoService;
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok("User banned");
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
        return ResponseEntity.ok("User unbanned");
    }

    @PutMapping("/users/{id}/promote")
    public ResponseEntity<String> promoteToModerator(@PathVariable Long id) {
        userService.promoteToModerator(id);
        return ResponseEntity.ok("User promoted to moderator");
    }

    @PutMapping("/users/{id}/demote")
    public ResponseEntity<String> demoteFromModerator(@PathVariable Long id) {
        userService.demoteFromModerator(id);
        return ResponseEntity.ok("Moderator demoted to user");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserAsModerator(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @DeleteMapping("/videos/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok("Video deleted successfully");
    }
}
