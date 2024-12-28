package Arthur.Code.MyTube_website_backend.dto.request;

import lombok.Getter;

@Getter
public class CommentRequest {
    private Long userId;
    private String content;

}
