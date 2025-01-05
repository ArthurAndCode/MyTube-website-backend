package Arthur.Code.MyTube_website_backend.dto.response;

import Arthur.Code.MyTube_website_backend.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String profilePictureUrl;
    private Role role;
}
