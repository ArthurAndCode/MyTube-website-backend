package Arthur.Code.MyTube_website_backend.dto.request;

import lombok.Data;

@Data
public class ChangeEmailRequest {
    private String email;
    private String password;
}
