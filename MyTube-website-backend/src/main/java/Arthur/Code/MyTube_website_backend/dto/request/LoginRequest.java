package Arthur.Code.MyTube_website_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
    private boolean rememberMeChecked;
}
