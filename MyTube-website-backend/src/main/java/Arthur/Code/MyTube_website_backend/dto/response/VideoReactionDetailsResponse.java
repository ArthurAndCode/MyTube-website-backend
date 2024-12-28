package Arthur.Code.MyTube_website_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VideoReactionDetailsResponse {
    private Long likeCount;
    private Long dislikeCount;
    private Boolean userLiked;
    private Boolean userDisliked;
    private Boolean userSubscribed;

}
