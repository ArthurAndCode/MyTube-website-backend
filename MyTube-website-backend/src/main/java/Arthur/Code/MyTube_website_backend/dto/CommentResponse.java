package Arthur.Code.MyTube_website_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private UserDTO user;
    private String content;
    private LocalDateTime createdAt;
}
