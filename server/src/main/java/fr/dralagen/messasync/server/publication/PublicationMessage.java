package fr.dralagen.messasync.server.publication;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Flow;

import org.springframework.http.MediaType;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import fr.dralagen.messasync.server.message.CreatedMessageEvent;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PublicationMessage {

    private List<SseEmitter> observers = new ArrayList<>();

    @ApplicationModuleListener
    void publishMessage(CreatedMessageEvent messageEvent) throws InterruptedException {
        log.info("Received a messageEvent({}) : {}", messageEvent.channel(), messageEvent.body());

        Thread.sleep(Duration.ofMillis((long) (Math.random() * 2500 + 500)));

        for (SseEmitter sseEmitter : observers) {
            try {
                sseEmitter.send(messageEvent, MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                observers.remove(sseEmitter);
                log.error("remove sseEmitter {}", sseEmitter, e);
            }
        }

        log.info("Published message({}) : {}", messageEvent.channel(), messageEvent.body());

    }

    public void subscribe(SseEmitter sseEmitter) {
        observers.add(sseEmitter);
        sseEmitter.onCompletion(() -> observers.remove(sseEmitter));
        sseEmitter.onTimeout(() -> observers.remove(sseEmitter));
        sseEmitter.onError((e) -> observers.remove(sseEmitter));
    }
}
