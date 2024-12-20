package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.CommentRequest;
import Arthur.Code.MyTube_website_backend.dto.CommentResponse;
import Arthur.Code.MyTube_website_backend.dto.PageableRequest;
import Arthur.Code.MyTube_website_backend.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos/{id}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    //nie zwracam video id, nie wiem czy tak zostanie
    @GetMapping()
    public ResponseEntity<Page<CommentResponse>> getVideoComments(@PathVariable Long id, @RequestBody PageableRequest pageableRequest) {
        Page<CommentResponse> comments = commentService.getVideoComments(id, pageableRequest);
        return ResponseEntity.ok(comments);
    }

    @PostMapping()
    public ResponseEntity<String> addComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
        commentService.addComment(id, commentRequest);
        return ResponseEntity.ok("Comment added successfully");
    }

}
