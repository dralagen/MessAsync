package fr.dralagen.messasync.server.publication;

import java.time.Duration;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import fr.dralagen.messasync.server.message.CreatedMessageEvent;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PublicationMessage {

    @ApplicationModuleListener
    void publishMessage(CreatedMessageEvent messageEvent) throws InterruptedException {
        log.info("Received a messageEvent({}) : {}", messageEvent.channel(), messageEvent.body());

        Thread.sleep(Duration.ofMillis((long) (Math.random() * 2500 + 500)));

        log.info("Published message({}) : {}", messageEvent.channel(), messageEvent.body());

    }
}
