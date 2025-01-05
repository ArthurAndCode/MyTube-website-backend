package Arthur.Code.MyTube_website_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long userId;
    private String content;

}
