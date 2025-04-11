package fr.dralagen.messasync.server.publication;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.MediaType;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import fr.dralagen.messasync.server.message.CreatedMessageEvent;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PublicationMessage {

    private List<SseEmitter> observers = new CopyOnWriteArrayList<>();

    @ApplicationModuleListener
    void publishMessage(CreatedMessageEvent messageEvent) throws InterruptedException {
        log.info("Received a messageEvent({}) : {}", messageEvent.channel(), messageEvent.body());

        Thread.sleep(Duration.ofMillis((long) (Math.random() * 2500 + 500)));

        AtomicInteger client = new AtomicInteger(0);

        observers.forEach(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event()
                    .id(String.valueOf(messageEvent.id()))
                    .name("createdMessage")
                    .data(messageEvent, MediaType.APPLICATION_JSON));

                client.incrementAndGet();
            } catch (Exception e) {
                log.debug("error to emit message into sseEmitter {}", sseEmitter, e);
                sseEmitter.completeWithError(e);
            }
        });

        log.info("Published message({}) to {} client(s) : {}", messageEvent.channel(), client.get(), messageEvent.body());

    }

    public void subscribe(SseEmitter sseEmitter) {
        observers.add(sseEmitter);
        sseEmitter.onCompletion(() -> {
            observers.remove(sseEmitter);
            log.info("Removed completed sseEmitter {}", sseEmitter);
        });
        sseEmitter.onTimeout(() -> {
            observers.remove(sseEmitter);
            log.info("Removed timeout sseEmitter {}", sseEmitter);
        });
        sseEmitter.onError((e) -> {
            observers.remove(sseEmitter);
            log.error("remove error sseEmitter {}", sseEmitter, e);
        });
    }

}
