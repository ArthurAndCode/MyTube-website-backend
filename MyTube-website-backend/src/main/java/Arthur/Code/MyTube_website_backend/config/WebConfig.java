package Arthur.Code.MyTube_website_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${uploads.base-dir}")
    private String baseDir;

    @Value("${uploads.thumbnails}")
    private String thumbnailsDir;

    @Value("${uploads.videos}")
    private String videosDir;

    @Value("${uploads.profile-pictures}")
    private String profilePicturesDir;

    private static final String FILE_PREFIX = "file:";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String thumbnailsPattern = baseDir + thumbnailsDir + "**";
        String thumbnailsLocation = FILE_PREFIX + baseDir + thumbnailsDir;

        String videosPattern = baseDir + videosDir + "**";
        String videosLocation = FILE_PREFIX + baseDir + videosDir;

        String profilePicturesPattern = baseDir + profilePicturesDir + "**";
        String profilePicturesLocation = FILE_PREFIX + baseDir + profilePicturesDir;

        registry.addResourceHandler(thumbnailsPattern)
                .addResourceLocations(thumbnailsLocation);

        registry.addResourceHandler(videosPattern)
                .addResourceLocations(videosLocation);

        registry.addResourceHandler(profilePicturesPattern)
                .addResourceLocations(profilePicturesLocation);
    }
}


