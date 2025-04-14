package fr.dralagen.messasync.server.publication;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import fr.dralagen.messasync.server.message.CreatedMessageEvent;
import fr.dralagen.messasync.server.publication.dto.MessageEvent;
import fr.dralagen.messasync.server.publication.service.MessageProcessingService;
import lombok.Getter;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ApplicationModuleTest
class PublicationMessageTest {

    @Autowired
    private PublicationMessage publicationMessage;

    @MockitoBean
    private MessageProcessingService messageProcessingService;

    @Test
    @Transactional
    void should_receiveMessage_whenSubribeToNewMessage(Scenario scenario) {
        // On crée un SseEmitter manuellement qui va capturer les événements
        TestSseEmitter testEmitter = new TestSseEmitter();

        // On l'ajoute directement au service qu'on veut tester
        publicationMessage.subscribe(testEmitter);

        // On prépare et publie un événement
        String messageId = "00421fe5-34e8-49f3-83a5-df6d95e6f8e6";
        String message = "Premier Message";
        String channel = "default";
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 14, 15, 36, 45);

        MessageEvent expectedEvent = new MessageEvent(messageId, message, channel, createdAt);

        when(messageProcessingService.processMessage(ArgumentMatchers.argThat(messageEvent -> messageId.equals(messageEvent.id()))))
            .thenReturn(expectedEvent);

        scenario.publish(new CreatedMessageEvent(
            UUID.fromString(messageId),
            message,
            channel,
            createdAt))
            .andWaitAtMost(Duration.ofSeconds(3))
            .andWaitForStateChange(() -> messageProcessingService.processMessage(expectedEvent));

        assertThat(testEmitter.getReceivedEvents())
            .filteredOn(dataWithMediaType -> MediaType.APPLICATION_JSON.equals(dataWithMediaType.getMediaType()))
            .singleElement()
            .extracting(ResponseBodyEmitter.DataWithMediaType::getData)
            .asInstanceOf(InstanceOfAssertFactories.type(MessageEvent.class))
            .returns(messageId, MessageEvent::id)
            .returns(message, MessageEvent::message)
            .returns(channel, MessageEvent::channel)
            .returns(createdAt, MessageEvent::createdAt);
    }

    // Classe pour capturer les événements SSE
    @Getter
    private static class TestSseEmitter extends SseEmitter {
        private final List<DataWithMediaType> receivedEvents = new ArrayList<>();

        public TestSseEmitter() {
            super(30000L); // timeout
        }

        @Override
        public void send(SseEventBuilder event) {
            Set<DataWithMediaType> build = event.build();
            receivedEvents.addAll(build);
        }

    }
}