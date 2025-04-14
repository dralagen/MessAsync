package fr.dralagen.messasync.server.publication;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.MediaType;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import fr.dralagen.messasync.server.message.CreatedMessageEvent;
import fr.dralagen.messasync.server.publication.dto.MessageEvent;
import fr.dralagen.messasync.server.publication.service.MessageProcessingService;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@EnableScheduling
public class PublicationMessage {

    private final List<SseEmitter> observers = new CopyOnWriteArrayList<>();

    private final MessageProcessingService messageProcessingService;

    public PublicationMessage(MessageProcessingService messageProcessingService) {
        this.messageProcessingService = messageProcessingService;
    }

    @ApplicationModuleListener
    void publishMessage(CreatedMessageEvent messageEvent) {
        log.info("Received a messageEvent({}) : {}", messageEvent.channel(), messageEvent.body());

        MessageEvent message = messageProcessingService.processMessage(convertEvent(messageEvent));

        AtomicInteger client = new AtomicInteger(0);

        observers.forEach(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event()
                    .id(String.valueOf(message.id()))
                    .name("createdMessage")
                    .data(message, MediaType.APPLICATION_JSON));

                client.incrementAndGet();
            } catch (Exception e) {
                log.debug("error to emit message into sseEmitter {}", sseEmitter, e);
                sseEmitter.completeWithError(e);
            }
        });

        log.info("Published message({}) to {} client(s) : {}", messageEvent.channel(), client.get(), messageEvent.body());

    }

    private static MessageEvent convertEvent(CreatedMessageEvent messageEvent) {
        return new MessageEvent(String.valueOf(messageEvent.id()), messageEvent.body(), messageEvent.body(), messageEvent.createdAt());
    }

    public void subscribe(SseEmitter sseEmitter) {
        observers.add(sseEmitter);
        sseEmitter.onCompletion(() -> {
            observers.remove(sseEmitter);
            log.debug("Removed completed sseEmitter {}", sseEmitter);
        });
        sseEmitter.onTimeout(() -> {
            observers.remove(sseEmitter);
            log.debug("Removed timeout sseEmitter {}", sseEmitter);
        });
        sseEmitter.onError((e) -> {
            observers.remove(sseEmitter);
            log.debug("remove error sseEmitter {}", sseEmitter, e);
        });
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    public void heartbeat() {
        observers.forEach(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().name("heartbeat"));
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            } catch (IllegalStateException e) {
                log.debug("emitter already closed");
            }
        });
    }

}
