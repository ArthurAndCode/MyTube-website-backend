package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.request.CommentRequest;
import Arthur.Code.MyTube_website_backend.dto.response.CommentResponse;
import Arthur.Code.MyTube_website_backend.dto.request.PageableRequest;
import Arthur.Code.MyTube_website_backend.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{videoId}/comments")
    public ResponseEntity<Page<CommentResponse>> getVideoComments(@PathVariable Long videoId, @RequestBody PageableRequest pageableRequest) {
        Page<CommentResponse> comments = commentService.getVideoComments(videoId, pageableRequest);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{videoId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long videoId, @RequestBody CommentRequest commentRequest) {
        commentService.addComment(videoId, commentRequest);
        return ResponseEntity.ok("Comment added successfully");
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

}
