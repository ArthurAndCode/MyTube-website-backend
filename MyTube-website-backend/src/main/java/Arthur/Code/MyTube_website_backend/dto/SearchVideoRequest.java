package Arthur.Code.MyTube_website_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchVideoRequest {
    private String title;
    private int page;
    private int size;
}