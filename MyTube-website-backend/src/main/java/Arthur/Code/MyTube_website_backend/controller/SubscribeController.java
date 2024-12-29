package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.response.SubscriptionDetailsResponse;
import Arthur.Code.MyTube_website_backend.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/subscriptions/{channelId}")
public class SubscribeController {

    private final SubscriptionService subscriptionService;

    public SubscribeController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping()
    public ResponseEntity<SubscriptionDetailsResponse> getSubscriptionsDetails(@PathVariable Long userId, @PathVariable Long channelId) {
        SubscriptionDetailsResponse response = subscriptionService.getSubscriptionsDetails(userId, channelId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping()
    public ResponseEntity<String> toggleSubscription(@PathVariable Long userId, @PathVariable Long channelId) {
        subscriptionService.toggleSubscription(userId, channelId);
        return ResponseEntity.ok().build();
    }
}
