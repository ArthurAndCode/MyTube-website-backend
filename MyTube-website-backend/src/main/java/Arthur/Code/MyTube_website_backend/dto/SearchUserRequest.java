package Arthur.Code.MyTube_website_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchUserRequest {
    private String username;
    private int page;
    private int size;
}
