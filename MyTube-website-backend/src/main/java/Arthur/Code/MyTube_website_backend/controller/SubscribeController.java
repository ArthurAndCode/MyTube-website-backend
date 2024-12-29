package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}")
public class SubscribeController {

    private final SubscriptionService subscriptionService;

    public SubscribeController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("subscribe/{channelId}")
    public ResponseEntity<String> subscribe(@PathVariable Long userId, @PathVariable Long channelId) {
        subscriptionService.toggleSubscription(userId, channelId);
        return ResponseEntity.ok().build();
    }

    //SUB CONTROLLER
    //sub
    //unsub
    //count sub
}
