package Arthur.Code.MyTube_website_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeEmailRequest {
    private String email;
    private String password;
}
