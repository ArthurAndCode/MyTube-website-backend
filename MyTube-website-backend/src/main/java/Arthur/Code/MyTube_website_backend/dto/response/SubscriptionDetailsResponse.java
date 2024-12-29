package Arthur.Code.MyTube_website_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDetailsResponse {
    private Long subscriptionsCount;
    private Boolean userSubscribed;
}
