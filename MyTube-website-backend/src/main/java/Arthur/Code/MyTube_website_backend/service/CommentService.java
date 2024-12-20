package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.CommentRequest;
import Arthur.Code.MyTube_website_backend.dto.CommentResponse;
import Arthur.Code.MyTube_website_backend.dto.PageableRequest;
import Arthur.Code.MyTube_website_backend.model.Comment;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    public Page<CommentResponse> getVideoComments(Long videoId, PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Comment> videos = commentRepository.findAllByVideoId(videoId, pageable);
        return videos.map(this::convertToCommentResponse);
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setUser(userService.convertToUserDTO((comment.getUser())));
        commentResponse.setCreatedAt(comment.getCreatedAt());
        return commentResponse;
    }

    public void addComment(Long videoId, CommentRequest commentRequest) {
        User user = userService.getUserById(commentRequest.getUserId());
        Comment comment = CreateCommentEntity(videoId, commentRequest, user);
        commentRepository.save(comment);
    }

    private static Comment CreateCommentEntity(Long videoId, CommentRequest commentRequest, User user) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setVideoId(videoId);
        comment.setContent(commentRequest.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }
}
