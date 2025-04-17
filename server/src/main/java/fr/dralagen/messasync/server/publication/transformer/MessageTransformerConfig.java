package fr.dralagen.messasync.server.publication.transformer;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.dralagen.messasync.server.publication.service.EmojifyService;

@Configuration
public class MessageTransformerConfig {

    @Bean
    public MessageTransformer messageTransformer(Optional<EmojifyService> emojifyService) {
        return emojifyService
            .map((Function<EmojifyService, MessageTransformer>) AiMessageTransformer::new)
            .orElseGet(ByPassMessageTransformer::new);
    }

}
