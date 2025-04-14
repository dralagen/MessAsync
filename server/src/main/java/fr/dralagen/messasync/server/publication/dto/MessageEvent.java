package fr.dralagen.messasync.server.publication.dto;

import java.time.LocalDateTime;

public record MessageEvent(String id, String message, String channel, LocalDateTime createdAt) {
}
