package fr.dralagen.messasync.server.message;

import java.time.LocalDateTime;

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
    public void sendMessage(@RequestBody MessageDto message) {
        events.publishEvent(new CreatedMessageEvent(message.message(), message.channel(), LocalDateTime.now()));
    }
}
