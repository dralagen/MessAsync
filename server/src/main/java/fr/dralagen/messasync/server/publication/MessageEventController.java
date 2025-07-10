package fr.dralagen.messasync.server.publication;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("message/event")
public class MessageEventController {

    public static final long SSE_EMITTER_TIMEOUT_MS = 10 * 60 * 1000;

    private final PublicationMessage publicationMessage;

    public MessageEventController(PublicationMessage publicationMessage) {
        this.publicationMessage = publicationMessage;
    }

    @GetMapping()
    public SseEmitter subscribeMessageEvent(@RequestParam(defaultValue = "default") String channel) throws IOException {
        log.info("create emitter for channel: {}", channel);
        SseEmitter sseEmitter = new SseEmitter(SSE_EMITTER_TIMEOUT_MS);
        publicationMessage.subscribe(sseEmitter, channel);

        sseEmitter.send(SseEmitter.event().name("heartbeat"));
        return sseEmitter;
    }

}
