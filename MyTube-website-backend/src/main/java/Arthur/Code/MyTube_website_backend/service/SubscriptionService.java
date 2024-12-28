package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.repository.SubscriptionRepository;
import Arthur.Code.MyTube_website_backend.repository.VideoRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final VideoRepository videoRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, VideoRepository videoRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.videoRepository = videoRepository;
    }

    public boolean isUserSubscribed(Long videoId, Long subscriberId) {
        Video video = getVideoByVideoId(videoId);
        Long channelId = video.getUser().getId();
        return subscriptionRepository.existsBySubscriberIdAndChannelId(subscriberId, channelId);
    }

    private Video getVideoByVideoId(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found."));
    }
}
