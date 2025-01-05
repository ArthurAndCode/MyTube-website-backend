package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.response.SubscriptionDetailsResponse;
import Arthur.Code.MyTube_website_backend.model.Subscription;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.repository.SubscriptionRepository;
import Arthur.Code.MyTube_website_backend.repository.UserRepository;
import Arthur.Code.MyTube_website_backend.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public void toggleSubscription(Long userId, Long channelId) {
        User subscriber = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        User channel = userRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found."));
        if (subscriber.getId().equals(channel.getId())) {
            throw new IllegalArgumentException("User ID and channel ID cannot be the same.");
        }

        Optional<Subscription> existingSubscription = findSubscription(userId, channelId);
        if (existingSubscription.isPresent()) {
            removeSubscription(existingSubscription.get());
        } else {
            createSubscription(subscriber, channel);
        }
    }

    private Optional<Subscription> findSubscription(Long userId, Long channelId) {
        return subscriptionRepository.findBySubscriberIdAndChannelId(userId, channelId);
    }

    private void removeSubscription(Subscription subscription) {
        subscriptionRepository.delete(subscription);
    }

    private void createSubscription(User subscriber, User channel) {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setChannel(channel);
        subscription.setCreatedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);
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

    public SubscriptionDetailsResponse getSubscriptionsDetails(Long userId, Long channelId) {
        List<Subscription> subscriptions = subscriptionRepository.findAllByChannelId(channelId);
        return calculateSubscriptions(userId, subscriptions);
    }

    private static SubscriptionDetailsResponse calculateSubscriptions(Long userId, List<Subscription> subscriptions) {
        long subscriptionsCount = 0;
        boolean userSubscribed = false;

        for (Subscription subscription : subscriptions) {
            subscriptionsCount++;
            if (subscription.getChannel().getId().equals(userId)) {
                userSubscribed = true;
            }
        }
        return createSubscriptionDetailsResponse(subscriptionsCount, userSubscribed);
    }

    private static SubscriptionDetailsResponse createSubscriptionDetailsResponse(long subscriptionsCount, boolean userSubscribed) {
        SubscriptionDetailsResponse response = new SubscriptionDetailsResponse();
        response.setSubscriptionsCount(subscriptionsCount);
        response.setUserSubscribed(userSubscribed);
        return response;
    }

}
