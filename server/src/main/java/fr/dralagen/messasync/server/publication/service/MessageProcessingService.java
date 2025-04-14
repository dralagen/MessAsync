package fr.dralagen.messasync.server.publication.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import fr.dralagen.messasync.server.publication.dto.MessageEvent;

@Service
public class MessageProcessingService {

    public MessageEvent processMessage(MessageEvent events) {

        try {
            Thread.sleep(Duration.ofMillis((long) (Math.random() * 2500 + 500)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return events;
    }

}
