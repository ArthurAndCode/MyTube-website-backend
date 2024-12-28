package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.response.VideoReactionDetailsResponse;
import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.model.VideoReaction;
import Arthur.Code.MyTube_website_backend.repository.VideoReactionRepository;
import Arthur.Code.MyTube_website_backend.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoReactionService {

    private final VideoReactionRepository videoReactionRepository;
    private final VideoRepository videoRepository;
    private final SubscriptionService subscriptionService;

    public VideoReactionService(VideoReactionRepository videoReactionRepository, VideoRepository videoRepository, SubscriptionService subscriptionService) {
        this.videoReactionRepository = videoReactionRepository;
        this.videoRepository = videoRepository;
        this.subscriptionService = subscriptionService;
    }

    private record ReactionResults(long likeCount, long dislikeCount, boolean userLiked, boolean userDisliked) {
    }

    public VideoReactionDetailsResponse getVideoReactionDetails(Long videoId, Long userId) {
        List<VideoReaction> reactions = videoReactionRepository.findAllByVideoId(videoId);
        ReactionResults results = calculateReactions(reactions, userId);
        boolean userSubscribed = subscriptionService.isUserSubscribed(videoId, userId);
        return createVideoReactionDetailsResponse(results, userSubscribed);
    }

    private ReactionResults calculateReactions(List<VideoReaction> reactions, Long userId) {
        long likeCount = 0;
        long dislikeCount = 0;
        boolean userLiked = false;
        boolean userDisliked = false;

        for (VideoReaction reaction : reactions) {
            likeCount += countLikes(reaction);
            dislikeCount += countDislikes(reaction);

            if (reaction.getUserId().equals(userId)) {
                userLiked = isUserLiked(reaction);
                userDisliked = isUserDisliked(reaction);
            }
        }
        return new ReactionResults(likeCount, dislikeCount, userLiked, userDisliked);
    }

    private long countLikes(VideoReaction reaction) {
        return reaction.getLiked() ? 1 : 0;
    }

    private long countDislikes(VideoReaction reaction) {
        return reaction.getDisliked() ? 1 : 0;
    }

    private boolean isUserLiked(VideoReaction reaction) {
        return reaction.getLiked();
    }

    private boolean isUserDisliked(VideoReaction reaction) {
        return reaction.getDisliked();
    }

    private VideoReactionDetailsResponse createVideoReactionDetailsResponse(ReactionResults results, boolean userSubscribed) {
        return new VideoReactionDetailsResponse(
                results.likeCount,
                results.dislikeCount,
                results.userLiked,
                results.userDisliked,
                userSubscribed
        );
    }

    public void reactToVideo(Long videoId, Long userId, String reaction) {
        Video video = getVideoByVideoId(videoId);
        VideoReaction videoReaction = videoReactionRepository.findByVideoIdAndUserId(videoId, userId)
                .orElseGet(() -> createNewReaction(video, userId));
        toggleReaction(reaction, videoReaction);
        videoReactionRepository.save(videoReaction);
    }

    private void toggleReaction(String reaction, VideoReaction videoReaction) {
        if ("like".equalsIgnoreCase(reaction)) {
            toggleLikeReaction(videoReaction);
        } else if ("dislike".equalsIgnoreCase(reaction)) {
            toggleDislikeReaction(videoReaction);
        }
    }

    private VideoReaction createNewReaction(Video video, Long userId) {
        VideoReaction reaction = new VideoReaction();
        reaction.setVideo(video);
        reaction.setUserId(userId);
        reaction.setLiked(false);
        reaction.setDisliked(false);
        return reaction;
    }

    private void toggleLikeReaction(VideoReaction reaction) {
        reaction.setLiked(!Boolean.TRUE.equals(reaction.getLiked()));
        reaction.setDisliked(false);
    }

    private void toggleDislikeReaction(VideoReaction reaction) {
        reaction.setDisliked(!Boolean.TRUE.equals(reaction.getDisliked()));
        reaction.setLiked(false);
    }

    private Video getVideoByVideoId(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found."));
    }

}
