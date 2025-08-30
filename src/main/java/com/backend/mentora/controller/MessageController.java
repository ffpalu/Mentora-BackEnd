package com.backend.mentora.controller;

import com.backend.mentora.dto.request.SendMessageRequest;
import com.backend.mentora.dto.response.MessageResponse;
import com.backend.mentora.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;

	public ResponseEntity<MessageResponse> sendMessage(
					@Valid @RequestBody SendMessageRequest request,
					Authentication auth
					){
		MessageResponse response = messageService.sendMessage(auth.getName(), request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/conversation/{userId}")
	public ResponseEntity<List<MessageResponse>> getConversation(
					@PathVariable Long userId,
					@RequestParam(defaultValue = "0") int page,
					@RequestParam(defaultValue = "10") int size,
					Authentication auth
	) {
		List<MessageResponse> messages = messageService
						.getConversation(auth.getName(), userId, page, size);
		return ResponseEntity.ok(messages);
	}



	@PutMapping("/{messageId}/read")
	public ResponseEntity<String> markAsRead(
					@PathVariable Long messageId,
					Authentication auth
	) {
		messageService.markAsRead(messageId, auth.getName());
		return ResponseEntity.ok("Message marked as read");
	}

	public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication auth) {
		Long count = messageService.getUnreadCount(auth.getName());
		return ResponseEntity.ok(Map.of("unreadCount", count));
	}

}
