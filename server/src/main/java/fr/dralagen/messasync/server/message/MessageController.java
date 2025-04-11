package fr.dralagen.messasync.server.message;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.dralagen.messasync.server.message.dto.MessageDto;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final ApplicationEventPublisher events;

    public MessageController(ApplicationEventPublisher applicationEventPublisher) {
        this.events = applicationEventPublisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CreatedMessageEvent sendMessage(@RequestBody MessageDto message) {
        CreatedMessageEvent event = new CreatedMessageEvent(UUID.randomUUID(), message.message(), message.channel(), LocalDateTime.now());
        events.publishEvent(event);

        return event;
    }
}
