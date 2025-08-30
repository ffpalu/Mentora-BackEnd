package com.backend.mentora.service;


import com.backend.mentora.dto.request.SendMessageRequest;
import com.backend.mentora.dto.response.MessageResponse;
import com.backend.mentora.entity.Message;
import com.backend.mentora.entity.User;
import com.backend.mentora.repository.MessageRepository;
import com.backend.mentora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;


	public MessageResponse sendMessage(String senderEmail, SendMessageRequest request){
		User sender = userRepository.findByEmail(senderEmail)
						.orElseThrow(() -> new RuntimeException("Sender not found"));

		User recipient = userRepository.findById(request.getRecipientId())
						.orElseThrow(() -> new RuntimeException("Recipient not found"));

		Message message = new Message();
		message.setSender(sender);
		message.setRecipient(recipient);
		message.setContent(request.getContent());

		Message saved = messageRepository.save(message);
		return mapToResponse(saved);

	}

	@Transactional(readOnly = true)
	public List<MessageResponse> getConversation(String userEmail, Long otherUserId, int page, int size) {
		User user = userRepository.findByEmail(userEmail)
						.orElseThrow(() -> new RuntimeException("User not found"));
		PageRequest pageRequest = PageRequest.of(page, size);

		List<Message> messages = messageRepository.findConversation(user.getId(), otherUserId, pageRequest);

		return messages.stream().map(this::mapToResponse).toList();
	}

	public void markAsRead(Long messageId, String userEmail) {
		Message message = messageRepository.findById(messageId)
						.orElseThrow(() -> new RuntimeException("Message not found"));
		if(!message.getRecipient().getEmail().equals(userEmail)) {
			throw new RuntimeException("Unauthorized");
		}

		message.setReadAt(LocalDateTime.now());
		messageRepository.saveAndFlush(message);

	}

	@Transactional(readOnly = true)
	public Long getUnreadCount(String userEmail) {
		User user = userRepository.findByEmail(userEmail)
						.orElseThrow(() -> new RuntimeException("User not found"));

		return messageRepository.countUnreadMessage(user.getId());
	}

	private MessageResponse mapToResponse(Message message) {
		return MessageResponse.builder()
						.id(message.getId())
						.senderId(message.getSender().getId())
						.senderName(message.getSender().getFullName())
						.recipientId(message.getRecipient().getId())
						.recipientName(message.getRecipient().getFullName())
						.content(message.getContent())
						.sentAt(message.getSentAt())
						.readAt(message.getReadAt())
						.isRead(message.isRead())
						.build();
	}

}
