package fr.dralagen.messasync.server.publication.transformer;

import fr.dralagen.messasync.server.publication.service.EmojifyService;

public class AiMessageTransformer implements MessageTransformer {
    private final EmojifyService emojifyService;

    public AiMessageTransformer(EmojifyService emojifyService) {
        this.emojifyService = emojifyService;
    }

    @Override
    public String transform(String message) {
        try {
        return emojifyService.transform(message);
        } catch (Exception e) {
            return message;
        }
    }
}
