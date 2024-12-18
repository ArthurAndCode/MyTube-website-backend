package Arthur.Code.MyTube_website_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageableRequest {
    private int page;
    private int size;
}
