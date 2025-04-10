package fr.dralagen.messasync.server.message;

import java.time.LocalDateTime;

public record CreatedMessageEvent(String body, String channel, LocalDateTime createdAt) {
}
