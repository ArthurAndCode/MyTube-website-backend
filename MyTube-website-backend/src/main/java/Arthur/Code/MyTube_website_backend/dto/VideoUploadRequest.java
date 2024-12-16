package Arthur.Code.MyTube_website_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Getter
@Setter
public class VideoUploadRequest {
    private MultipartFile video;
    private MultipartFile thumbnail;
    private String title;
    private String description;
    private Long userId;
}
