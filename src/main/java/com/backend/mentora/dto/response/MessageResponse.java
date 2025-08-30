package com.backend.mentora.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
	private Long id;
	private Long senderId;
	private String senderName;
	private Long recipientId;
	private String recipientName;
	private String content;
	private LocalDateTime sentAt;
	private LocalDateTime readAt;
	private Boolean isRead;
}
