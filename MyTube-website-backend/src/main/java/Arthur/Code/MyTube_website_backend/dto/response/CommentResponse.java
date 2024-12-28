package Arthur.Code.MyTube_website_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private UserResponse user;
    private String content;
    private LocalDateTime createdAt;
}
