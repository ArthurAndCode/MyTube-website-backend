package Arthur.Code.MyTube_website_backend.repository;

import Arthur.Code.MyTube_website_backend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsBySubscriberIdAndChannelId(Long subscriberId, Long channelId);
    Optional<Subscription> findBySubscriberIdAndChannelId(Long subscriberId, Long channelId);
    List<Subscription> findAllByChannelId(Long channelId);

}
