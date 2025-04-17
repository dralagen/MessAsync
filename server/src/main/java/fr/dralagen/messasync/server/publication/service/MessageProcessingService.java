package fr.dralagen.messasync.server.publication.service;

import org.springframework.stereotype.Service;

import fr.dralagen.messasync.server.publication.dto.MessageEvent;
import fr.dralagen.messasync.server.publication.transformer.MessageTransformer;

@Service
public class MessageProcessingService {

    private final MessageTransformer messageTransformer;

    public MessageProcessingService(MessageTransformer messageTransformer) {
        this.messageTransformer = messageTransformer;
    }

    public MessageEvent processMessage(MessageEvent events) {

        String enrichedMessage = messageTransformer.transform(events.message());

        return new MessageEvent(events.id(), enrichedMessage, events.channel(), events.createdAt());

    }
}
