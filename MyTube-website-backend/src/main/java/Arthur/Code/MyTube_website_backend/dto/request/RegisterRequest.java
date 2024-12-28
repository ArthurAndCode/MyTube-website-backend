package Arthur.Code.MyTube_website_backend.dto.request;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
