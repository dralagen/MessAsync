package fr.dralagen.messasync.server.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatedMessageEvent(UUID id, String body, String channel, LocalDateTime createdAt) {
}
