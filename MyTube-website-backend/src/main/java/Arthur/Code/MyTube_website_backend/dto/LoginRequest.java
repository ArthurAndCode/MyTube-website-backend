package Arthur.Code.MyTube_website_backend.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
    private boolean rememberMeChecked;
}
