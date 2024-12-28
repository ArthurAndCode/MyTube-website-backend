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
    //nie zwracam video id, nie wiem czy tak zostanie
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentResponse>> getVideoComments(@PathVariable Long id, @RequestBody PageableRequest pageableRequest) {
        Page<CommentResponse> comments = commentService.getVideoComments(id, pageableRequest);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
        commentService.addComment(id, commentRequest);
        return ResponseEntity.ok("Comment added successfully");
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }

}
