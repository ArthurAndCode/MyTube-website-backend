package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.repository.InteractionRepository;
import org.springframework.stereotype.Service;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;

    public InteractionService(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }
}
