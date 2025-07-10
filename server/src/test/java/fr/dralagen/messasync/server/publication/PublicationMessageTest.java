package fr.dralagen.messasync.server.publication;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.dralagen.messasync.server.message.CreatedMessageEvent;
import fr.dralagen.messasync.server.publication.dto.MessageEvent;
import fr.dralagen.messasync.server.publication.service.MessageProcessingService;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ApplicationModuleTest
class PublicationMessageTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Autowired
    private PublicationMessage publicationMessage;

    @MockitoBean
    private MessageProcessingService messageProcessingService;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        Map<String, List<SseEmitter>> observersByChannel = getObserversByChannel();
        assertThat(observersByChannel).isNotNull();
        observersByChannel.clear();
    }

    @Test
    @Transactional
    void should_receiveMessage_whenSubribeToNewMessage(Scenario scenario) throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/message/event"))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(mvcResult.getResponse().getContentType()).contains(MediaType.TEXT_EVENT_STREAM_VALUE);

        // On prépare et publie un événement
        String messageId = "00421fe5-34e8-49f3-83a5-df6d95e6f8e6";
        String message = "Premier Message";
        String channel = "default";
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 14, 15, 36, 45);

        CreatedMessageEvent event = new CreatedMessageEvent(
            UUID.fromString(messageId),
            message,
            channel,
            createdAt);
        MessageEvent expectedEvent = new MessageEvent(messageId, message, channel, createdAt);

        mockProcessMessage(expectedEvent);

        scenario.publish(event)
            .andWaitAtMost(Duration.ofSeconds(3))
            .andWaitForStateChange(() -> messageProcessingService.processMessage(expectedEvent));

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).contains("id:" + messageId)
            .contains("data:" + OBJECT_MAPPER.writeValueAsString(expectedEvent));

    }

    @Test
    @Transactional
    void should_receiveMessage_whenSubribeToNewMessageOnChannelEmoji(Scenario scenario) throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/message/event?channel=emoji"))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(mvcResult.getResponse().getContentType()).contains(MediaType.TEXT_EVENT_STREAM_VALUE);

        // On prépare et publie un événement
        String messageId = "00421fe5-34e8-49f3-83a5-df6d95e6f8e6";
        String message = "Premier Message";
        String channel = "emoji";
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 14, 15, 36, 45);

        CreatedMessageEvent event = new CreatedMessageEvent(
            UUID.fromString(messageId),
            message,
            channel,
            createdAt);
        MessageEvent expectedEvent = new MessageEvent(messageId, message, channel, createdAt);

        mockProcessMessage(expectedEvent);

        scenario.publish(event)
            .andWaitAtMost(Duration.ofSeconds(3))
            .andWaitForStateChange(() -> messageProcessingService.processMessage(expectedEvent));

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).contains("id:" + messageId)
            .contains("data:" + OBJECT_MAPPER.writeValueAsString(expectedEvent));

    }

    @Test
    @Transactional
    void should_publishMessageToCorrectChannelOnly_whenMultipleChannelsExist(Scenario scenario) throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/message/event"))
            .andExpect(status().isOk())
            .andReturn();

        MvcResult mvcResultEmoji = mockMvc.perform(get("/message/event?channel=emoji"))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(mvcResult.getResponse().getContentType()).contains(MediaType.TEXT_EVENT_STREAM_VALUE);

        // On prépare et publie un événement
        String messageId = "00421fe5-34e8-49f3-83a5-df6d95e6f8e6";
        String message = "Premier Message";
        String channel = "emoji";
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 14, 15, 36, 45);

        CreatedMessageEvent event = new CreatedMessageEvent(
            UUID.fromString(messageId),
            message,
            channel,
            createdAt);
        MessageEvent expectedEvent = new MessageEvent(messageId, message, channel, createdAt);

        mockProcessMessage(expectedEvent);

        scenario.publish(event)
            .andWaitAtMost(Duration.ofSeconds(3))
            .andWaitForStateChange(() -> messageProcessingService.processMessage(expectedEvent));

        String content = mvcResult.getResponse().getContentAsString();
        String contentEmoji = mvcResultEmoji.getResponse().getContentAsString();

        assertThat(content).doesNotContain("id:");
        assertThat(contentEmoji).contains("id:" + messageId)
            .contains("data:" + OBJECT_MAPPER.writeValueAsString(expectedEvent));

    }

    @Test
    void shouldNoError_onHeartBeat_whenRequestIsOpen() {
        Map<String, List<SseEmitter>> observersByChannel = getObserversByChannel();
        assertThat(observersByChannel).isEmpty();

        SseEmitter sseEmitter = new SseEmitter();
        publicationMessage.subscribe(sseEmitter, "default");

        assertThat(observersByChannel).hasSize(1);
        assertThat(observersByChannel.get("default")).hasSize(1);

        publicationMessage.heartbeat();

        assertThat(observersByChannel).hasSize(1);
        assertThat(observersByChannel.get("default")).hasSize(1);
    }

    @Test
    void shouldRemoveSseEmitter_whenHaveCompleted() throws Exception {
        Map<String, List<SseEmitter>> observersByChannel = getObserversByChannel();
        assertThat(observersByChannel).isEmpty();

        MvcResult mvcResult = mockMvc.perform(get("/message/event"))
            .andExpect(status().isOk())
            .andReturn();

        Map<String, List<SseEmitter>> observersByChannelAfterSubscribe = getObserversByChannel();
        assertThat(observersByChannelAfterSubscribe).hasSize(1);
        assertThat(observersByChannelAfterSubscribe.get("default")).hasSize(1);

        Objects.requireNonNull(mvcResult.getRequest().getAsyncContext()).complete();

        Map<String, List<SseEmitter>> observersByChannelAfterComplete = getObserversByChannel();
        assertThat(observersByChannelAfterComplete).isEmpty();
    }

    @Test
    void shouldRemoveSseEmitter_onHeartbeat_whenAlreadyClose() throws IOException {
        Map<String, List<SseEmitter>> observersByChannel = getObserversByChannel();
        assertThat(observersByChannel).isEmpty();

        SseEmitter sseEmitter = new SseEmitter();
        publicationMessage.subscribe(sseEmitter, "default");
        assertThat(observersByChannel).hasSize(1);
        assertThat(observersByChannel.get("default")).hasSize(1);

        sseEmitter.complete();

        publicationMessage.heartbeat();

        assertThat(observersByChannel).isEmpty();

    }

    private void mockProcessMessage(MessageEvent expectedEvent) {
        when(
            messageProcessingService.processMessage(ArgumentMatchers.argThat(messageEvent -> expectedEvent.id().equals(messageEvent.id()))))
            .thenReturn(expectedEvent);
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<SseEmitter>> getObserversByChannel() {
        return (Map<String, List<SseEmitter>>) ReflectionTestUtils.getField(publicationMessage, "observersByChannel");
    }
}
