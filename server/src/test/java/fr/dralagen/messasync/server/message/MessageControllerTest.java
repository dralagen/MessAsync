package fr.dralagen.messasync.server.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ApplicationModuleTest
class MessageControllerTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    @Autowired
    MockMvc mockMvc;

    @Test
    void when_sendMessage_should_returnHttpCreated_and_sendEvent(AssertablePublishedEvents events) throws Exception {
        MvcResult result = mockMvc.perform(post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "message": "Premier message",
                        "channel": "general"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        CreatedMessageEvent responseBody =
            OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), CreatedMessageEvent.class);

        assertThat(responseBody)
            .isNotNull()
            .returns("Premier message", CreatedMessageEvent::body)
            .returns("general", CreatedMessageEvent::channel)
            .extracting(CreatedMessageEvent::id, CreatedMessageEvent::createdAt)
            .isNotNull();

        assertThat(events)
            .contains(CreatedMessageEvent.class)
            .matching(CreatedMessageEvent::body, "Premier message")
            .matching(CreatedMessageEvent::channel, "general");
    }
}